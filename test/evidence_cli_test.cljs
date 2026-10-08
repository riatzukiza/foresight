;; SPDX-License-Identifier: GPL-3.0-or-later
(ns evidence-cli-test
  (:require [cljs.test :as test :refer [deftest is]]
            [clojure.string :as str]
            [evidence :as cli]
            [foresight.evidence :as law]
            [workspace :as workspace]
            ["child_process" :as child-process]
            ["fs" :as fs]
            ["os" :as os]
            ["path" :as path]))

(def test-catalog-identity
  {:catalog/path "config/quality-gates.edn"
   :catalog/sha256 (apply str (repeat 64 "a"))})

(def child-revision (apply str (repeat 40 "1")))

(def sha256-child-revision (apply str (repeat 64 "3")))

(def reviewed-root-revision (apply str (repeat 40 "c")))

(def trusted-base-revision (apply str (repeat 40 "e")))

(def blob-revision (apply str (repeat 40 "b")))

(defn regular-blob-entry [repository-path]
  (str "100644 blob " blob-revision "\t" repository-path "\u0000"))

(def local-gate
  {:gate/id :repo/unit
   :gate/kind :unit
   :gate/execution :local
   :gate/command ["test" "--exact"]
   :gate/source "repo/package.json"})

(def passed-result
  {:gate/id :repo/unit
   :result/outcome :passed
   :result/exit 0
   :result/revision child-revision
   :result/execution :local
   :result/command ["test" "--exact"]
   :result/catalog test-catalog-identity
   :result/source {:source/path "repo/package.json"
                   :source/repository "repo"
                   :source/revision child-revision}})

(defn with-receipt-fixture [run!]
  (let [fixture (fs/mkdtempSync
                 (path/join (os/tmpdir) "foresight-receipt-append-"))
        directory (path/join fixture ".ημ")
        file (path/join directory "receipts.edn")]
    (fs/mkdirSync directory)
    (try
      (run! {:fixture fixture :directory directory :file file})
      (finally
        (fs/rmSync fixture #js {:recursive true :force true})))))

(deftest secure-append-creates-and-serializes-regular-receipts
  (with-receipt-fixture
    (fn [{:keys [file]}]
      (let [first-record {:kind :observation :note "first"}
            second-record {:kind :test-run :tests ["pass"]}]
        (is (= first-record (cli/append-edn-line! file first-record)))
        (is (= (str (pr-str first-record) "\n")
               (fs/readFileSync file "utf8")))
        (is (= second-record (cli/append-edn-line! file second-record)))
        (is (= (str (pr-str first-record) "\n"
                    (pr-str second-record) "\n")
               (fs/readFileSync file "utf8")))
        (is (.isFile (fs/lstatSync file)))
        (is (= 1 (.-nlink (fs/lstatSync file))))))))

(deftest secure-append-repairs-a-missing-terminal-newline
  (with-receipt-fixture
    (fn [{:keys [file]}]
      (let [first-record {:kind :observation :note "unterminated"}
            second-record {:kind :test-run :note "bounded"}]
        (fs/writeFileSync file (pr-str first-record) "utf8")
        (cli/append-edn-line! file second-record)
        (is (= (str (pr-str first-record) "\n"
                    (pr-str second-record) "\n")
               (fs/readFileSync file "utf8")))))))

(deftest secure-append-rejects-a-final-symlink
  (with-receipt-fixture
    (fn [{:keys [fixture file]}]
      (let [outside (path/join fixture "outside.edn")]
        (fs/writeFileSync outside "{:outside true}\n" "utf8")
        (fs/symlinkSync outside file "file")
        (is (thrown-with-msg?
             js/Error #"Receipt River file must not be a symbolic link"
             (cli/append-edn-line! file {:inside true})))
        (is (= "{:outside true}\n" (fs/readFileSync outside "utf8")))))))

(deftest secure-append-rejects-a-parent-symlink
  (let [fixture (fs/mkdtempSync
                 (path/join (os/tmpdir) "foresight-receipt-parent-"))
        actual (path/join fixture "actual")
        linked (path/join fixture ".ημ")
        file (path/join linked "receipts.edn")]
    (fs/mkdirSync actual)
    (fs/symlinkSync actual linked "dir")
    (try
      (is (thrown-with-msg?
           js/Error #"parent directory must not be a symbolic link"
           (cli/append-edn-line! file {:unsafe true})))
      (is (not (fs/existsSync (path/join actual "receipts.edn"))))
      (finally
        (fs/rmSync fixture #js {:recursive true :force true})))))

(deftest secure-append-fails-closed-when-the-writer-lock-exists
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [lock-file (path/join directory ".receipts.edn.append.lock")]
        (fs/writeFileSync lock-file "held-by-another-writer\n"
                          #js {:encoding "utf8" :flag "wx"})
        (is (thrown-with-msg?
             js/Error #"another writer holds the append lock"
             (cli/append-edn-line! file {:contended true})))
        (is (not (fs/existsSync file)))
        (is (= "held-by-another-writer\n"
               (fs/readFileSync lock-file "utf8")))))))

(deftest secure-append-serializes-a-reentrant-writer
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [contender-error (atom nil)
            attempted? (atom false)
            winning-record {:writer :first}
            losing-record {:writer :second}]
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (and (= :before-write phase) (not @attempted?))
                      (reset! attempted? true)
                      (try
                        (cli/append-edn-line! file losing-record)
                        (catch :default error
                          (reset! contender-error error)))))]
          (is (= winning-record
                 (cli/append-edn-line! file winning-record))))
        (is @attempted?)
        (is (instance? js/Error @contender-error))
        (is (re-find #"another writer holds the append lock"
                     (.-message @contender-error)))
        (is (= (str (pr-str winning-record) "\n")
               (fs/readFileSync file "utf8")))
        (is (not (fs/existsSync
                  (path/join directory ".receipts.edn.append.lock"))))))))

(deftest secure-append-lock-excludes-a-separate-process
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [contender (atom nil)
            attempted? (atom false)
            lock-file (path/join directory ".receipts.edn.append.lock")
            script (str
                    "const fs=require('fs');"
                    "const c=fs.constants;"
                    "try {"
                    "fs.openSync(process.argv[1],"
                    "c.O_WRONLY|c.O_CREAT|c.O_EXCL|c.O_NOFOLLOW,0o600);"
                    "process.exit(7);"
                    "} catch (error) {"
                    "process.exit(error.code==='EEXIST'?0:8);"
                    "}")]
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (and (= :before-write phase) (not @attempted?))
                      (reset! attempted? true)
                      (reset! contender
                              (child-process/spawnSync
                               "node" #js ["-e" script lock-file]
                               #js {:encoding "utf8" :shell false}))))]
          (is (= {:writer :parent}
                 (cli/append-edn-line! file {:writer :parent}))))
        (is @attempted?)
        (is (= 0 (.-status @contender)))
        (is (= (str (pr-str {:writer :parent}) "\n")
               (fs/readFileSync file "utf8")))
        (is (not (fs/existsSync lock-file)))))))

(deftest secure-append-rejects-a-final-file-swap-before-writing
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [original "{:original true}\n"
            replacement "{:replacement true}\n"
            moved (path/join directory "original.edn")]
        (fs/writeFileSync file original "utf8")
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (= :before-write phase)
                      (fs/renameSync file moved)
                      (fs/writeFileSync file replacement "utf8")))]
          (is (thrown-with-msg?
               js/Error #"Receipt River file identity changed"
               (cli/append-edn-line! file {:must-not-append true}))))
        (is (= original (fs/readFileSync moved "utf8")))
        (is (= replacement (fs/readFileSync file "utf8")))
        (is (not (fs/existsSync
                  (path/join directory ".receipts.edn.append.lock"))))))))

(deftest secure-append-rejects-a-parent-swap-before-writing
  (with-receipt-fixture
    (fn [{:keys [fixture directory file]}]
      (let [original "{:original true}\n"
            moved (path/join fixture "moved-parent")]
        (fs/writeFileSync file original "utf8")
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (= :before-write phase)
                      (fs/renameSync directory moved)
                      (fs/mkdirSync directory)))]
          (is (thrown-with-msg?
               js/Error #"parent directory identity changed"
               (cli/append-edn-line! file {:must-not-append true}))))
        (is (= original
               (fs/readFileSync (path/join moved "receipts.edn") "utf8")))
        (is (not (fs/existsSync file)))
        (is (not (fs/existsSync
                  (path/join moved ".receipts.edn.append.lock"))))))))

(deftest secure-append-quarantines-a-post-write-file-swap
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [record {:append :durable-but-path-swapped}
            original "{:original true}\n"
            replacement "{:replacement true}\n"
            moved (path/join directory "written-before-swap.edn")
            lock-file (path/join directory ".receipts.edn.append.lock")]
        (fs/writeFileSync file original "utf8")
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (= :after-write phase)
                      (fs/renameSync file moved)
                      (fs/writeFileSync file replacement "utf8")))]
          (is (thrown-with-msg?
               js/Error #"Receipt River file identity changed"
               (cli/append-edn-line! file record))))
        (is (= (str original (pr-str record) "\n")
               (fs/readFileSync moved "utf8")))
        (is (= replacement (fs/readFileSync file "utf8")))
        (is (fs/existsSync lock-file))
        (is (thrown-with-msg?
             js/Error #"another writer holds the append lock"
             (cli/append-edn-line! file {:blocked :until-adjudicated})))))))

(deftest secure-append-quarantines-a-post-write-in-place-overwrite
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [record {:append :durable-but-prefix-overwritten}
            original "{:original true}\n"
            replacement "{:forgedxx true}\n"
            lock-file (path/join directory ".receipts.edn.append.lock")]
        (is (= (.-byteLength (js/Buffer.from original "utf8"))
               (.-byteLength (js/Buffer.from replacement "utf8"))))
        (fs/writeFileSync file original "utf8")
        (binding [cli/*secure-append-phase-hook*
                  (fn [phase _context]
                    (when (= :after-write phase)
                      (let [fd (fs/openSync file "r+")
                            bytes (js/Buffer.from replacement "utf8")]
                        (try
                          (fs/writeSync fd bytes 0 (.-byteLength bytes) 0)
                          (fs/fsyncSync fd)
                          (finally
                            (fs/closeSync fd))))))]
          (is (thrown-with-msg?
               js/Error #"changed after append"
               (cli/append-edn-line! file record))))
        (is (= (str replacement (pr-str record) "\n")
               (fs/readFileSync file "utf8")))
        (is (fs/existsSync lock-file))
        (is (thrown-with-msg?
             js/Error #"another writer holds the append lock"
             (cli/append-edn-line! file {:blocked :until-adjudicated})))))))

(deftest secure-append-rejects-oversized-receipt-lines-before-opening
  (with-receipt-fixture
    (fn [{:keys [file]}]
      (let [oversized {:note (.repeat "x" (inc cli/max-receipt-line-bytes))}]
        (is (thrown-with-msg?
             js/Error #"receipt exceeds"
             (cli/append-edn-line! file oversized)))
        (is (not (fs/existsSync file)))))))

(deftest parses-explicit-repository-and-kind-selection
  (is (= {:only #{"katamorph"}
         :kinds #{:unit :static}
          :at nil
          :base nil}
         (cli/parse-args ["--only" "katamorph"
                          "--kind" "unit,static"])))
  (is (= (apply str (repeat 40 "a"))
         (:at (cli/parse-args
               ["--at" (apply str (repeat 40 "a"))]))))
  (is (= reviewed-root-revision
         (:base (cli/parse-args ["--base" reviewed-root-revision]))))
  (is (thrown-with-msg? js/Error #"Unknown gate kinds"
                        (cli/parse-args ["--kind" "pretend-e2e"]))))

(deftest appends-complete-receipt-river-evidence
  (let [captured (atom nil)]
    (with-redefs [cli/with-append-reservation!
                  (fn [file validate-ledger? append!]
                    (is validate-ledger?)
                    (append! {:file file}))
                  cli/append-reserved-edn-line!
                  (fn [reservation receipt line]
                    (reset! captured {:file (:file reservation)
                                      :receipt receipt
                                      :line line})
                    receipt)]
      (let [receipt (cli/append-evidence-receipt! passed-result)]
        (is (= receipt (:receipt @captured)))
        (is (= (pr-str receipt) (:line @captured)))
        (is (law/evidence-receipt? receipt))
        (is (= 2 (:evidence/schema receipt)))
        (is (re-find #"^nbb/node@v" (:evidence/adapter receipt)))
        (is (not= "local" (:host receipt)))
        (is (= passed-result (:evidence/result receipt)))
        (is (= [(str "repo@" child-revision) ":repo/unit"]
               (:refs receipt)))
        (is (re-find #"[.]ημ/receipts[.]edn$" (:file @captured)))))))

(deftest evidence-append-refuses-to-recreate-a-missing-ledger
  (with-receipt-fixture
    (fn [{:keys [file]}]
      (with-redefs [cli/receipt-file file]
        (is (thrown-with-msg?
             js/Error #"could not be securely opened"
             (cli/append-evidence-receipt! passed-result)))
        (is (not (fs/existsSync file)))))))

(deftest gate-runs-preflight-durable-receipt-support
  (let [calls (atom [])
        catalog {:catalog/repositories
                 {"repo" {:repository/path "repo"
                          :repository/gates [local-gate]}}}]
    (with-redefs [cli/require-secure-append-support!
                  (fn []
                    (swap! calls conj :preflight)
                    (throw (js/Error. "unsupported receipt host")))
                  cli/require-repositories!
                  (fn [& _]
                    (swap! calls conj :repositories)
                    {})
                  cli/run-gate!
                  (fn [& _]
                    (swap! calls conj :gate)
                    passed-result)]
      (is (thrown-with-msg?
           js/Error #"unsupported receipt host"
           (cli/run-selected-gates!
            catalog test-catalog-identity
            {:only #{"repo"} :kinds #{:unit}})))
      (is (= [:preflight] @calls)))))

(deftest selected-gates-hold-the-receipt-reservation-before-execution
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [gate-ran? (atom false)
            lock-file (path/join directory ".receipts.edn.append.lock")
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        ;; Semantic evidence appends require an explicitly initialized ledger.
        (fs/writeFileSync file "" "utf8")
        (with-redefs [cli/receipt-file file
                      cli/git-capture!
                      (fn [args]
                        (is (= ["rev-parse" "HEAD"] args))
                        reviewed-root-revision)
                      cli/read-immutable-receipt-ledger!
                      (fn [revision]
                        (is (= reviewed-root-revision revision))
                        {:ledger/bytes (js/Buffer.alloc 0)})
                      cli/require-repositories!
                      (fn [& _]
                        {"repo" {:path "repo"
                                 :absolute "/repo"
                                 :revision child-revision}})
                      cli/run-gate!
                      (fn [& _]
                        (reset! gate-ran? true)
                        (is (fs/existsSync lock-file))
                        (is (fs/existsSync file))
                        passed-result)]
          (is (zero? (cli/run-selected-gates!
                      catalog test-catalog-identity
                      {:only #{"repo"} :kinds #{:unit}})))
          (is @gate-ran?)
          (is (not (fs/existsSync lock-file)))
          (let [records (cli/read-receipt-records!
                         (fs/readFileSync file "utf8"))]
            (is (= 1 (count records)))
            (is (= passed-result (:evidence/result (first records))))))))))

(deftest selected-gates-reject-an-unsafe-receipt-target-before-execution
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [gate-ran? (atom false)
            lock-file (path/join directory ".receipts.edn.append.lock")
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        (fs/writeFileSync lock-file "stale-or-held\n"
                          #js {:encoding "utf8" :flag "wx"})
        (with-redefs [cli/receipt-file file
                      cli/require-repositories!
                      (fn [& _]
                        {"repo" {:path "repo"
                                 :absolute "/repo"
                                 :revision child-revision}})
                      cli/run-gate!
                      (fn [& _]
                        (reset! gate-ran? true)
                        passed-result)]
          (is (thrown-with-msg?
               js/Error #"another writer holds the append lock"
               (cli/run-selected-gates!
                catalog test-catalog-identity
                {:only #{"repo"} :kinds #{:unit}})))
          (is (false? @gate-ran?))
          (is (not (fs/existsSync file)))
          (is (= "stale-or-held\n"
                 (fs/readFileSync lock-file "utf8"))))))))

(deftest selected-gates-refuse-to-recreate-a-missing-ledger
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [gate-ran? (atom false)
            lock-file (path/join directory ".receipts.edn.append.lock")
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        (with-redefs [cli/receipt-file file
                      cli/require-repositories!
                      (fn [& _]
                        {"repo" {:path "repo"
                                 :absolute "/repo"
                                 :revision child-revision}})
                      cli/run-gate!
                      (fn [& _]
                        (reset! gate-ran? true)
                        passed-result)]
          (is (thrown-with-msg?
               js/Error #"could not be securely opened"
               (cli/run-selected-gates!
                catalog test-catalog-identity
                {:only #{"repo"} :kinds #{:unit}})))
          (is (false? @gate-ran?))
          (is (not (fs/existsSync file)))
          (is (not (fs/existsSync lock-file))))))))

(deftest selected-gates-reject-a-valid-truncated-committed-ledger
  (let [first-record {:ts "2026-08-29T22:58:48Z"
                      :kind :decision
                      :repo "."
                      :origin "test"
                      :owner "test"
                      :dod "preserve first"
                      :pi "eta-mu"
                      :host "test"
                      :manifest ["test"]
                      :refs ["first"]}
        second-record (assoc first-record
                             :ts "2026-08-29T22:58:49Z"
                             :dod "preserve second"
                             :refs ["second"])
        first-line (str (pr-str first-record) "\n")
        committed-text (str first-line (pr-str second-record) "\n")
        committed-bytes (js/Buffer.from committed-text "utf8")
        catalog {:catalog/repositories
                 {"repo" {:repository/path "repo"
                          :repository/gates [local-gate]}}}]
    (is (law/receipt-envelope? first-record))
    (is (law/receipt-envelope? second-record))
    (doseq [held-text ["" first-line]]
      (with-receipt-fixture
        (fn [{:keys [directory file]}]
          (let [gate-ran? (atom false)
                lock-file (path/join directory
                                     ".receipts.edn.append.lock")]
            (fs/writeFileSync file held-text "utf8")
            (with-redefs [cli/receipt-file file
                          cli/git-capture!
                          (fn [args]
                            (is (= ["rev-parse" "HEAD"] args))
                            reviewed-root-revision)
                          cli/read-immutable-receipt-ledger!
                          (fn [revision]
                            (is (= reviewed-root-revision revision))
                            {:ledger/bytes committed-bytes})
                          cli/require-repositories!
                          (fn [& _]
                            {"repo" {:path "repo"
                                     :absolute "/repo"
                                     :revision child-revision}})
                          cli/run-gate!
                          (fn [& _]
                            (reset! gate-ran? true)
                            passed-result)]
              (is (thrown-with-msg?
                   js/Error #"does not preserve the committed ledger"
                   (cli/run-selected-gates!
                    catalog test-catalog-identity
                    {:only #{"repo"} :kinds #{:unit}})))
              (is (false? @gate-ran?))
              (is (= held-text (fs/readFileSync file "utf8")))
              (is (not (fs/existsSync lock-file))))))))))

(deftest selected-gates-reject-an-invalid-held-ledger-before-execution
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [gate-ran? (atom false)
            invalid-ledger "{:kind :observation}\n"
            lock-file (path/join directory ".receipts.edn.append.lock")
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        (fs/writeFileSync file invalid-ledger "utf8")
        (with-redefs [cli/receipt-file file
                      cli/git-capture!
                      (fn [_] reviewed-root-revision)
                      cli/read-immutable-receipt-ledger!
                      (fn [_]
                        {:ledger/bytes (js/Buffer.alloc 0)})
                      cli/require-repositories!
                      (fn [& _]
                        {"repo" {:path "repo"
                                 :absolute "/repo"
                                 :revision child-revision}})
                      cli/run-gate!
                      (fn [& _]
                        (reset! gate-ran? true)
                        passed-result)]
          (is (thrown-with-msg?
               js/Error #"invalid receipt envelopes"
               (cli/run-selected-gates!
                catalog test-catalog-identity
                {:only #{"repo"} :kinds #{:unit}})))
          (is (false? @gate-ran?))
          (is (= invalid-ledger (fs/readFileSync file "utf8")))
          (is (not (fs/existsSync lock-file))))))))

(deftest selected-gates-reject-held-ledger-mutation-after-execution
  (with-receipt-fixture
    (fn [{:keys [directory file]}]
      (let [gate-ran? (atom false)
            changed-ledger "{:kind :observation}\n"
            lock-file (path/join directory ".receipts.edn.append.lock")
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        (fs/writeFileSync file "" "utf8")
        (with-redefs [cli/receipt-file file
                      cli/git-capture!
                      (fn [_] reviewed-root-revision)
                      cli/read-immutable-receipt-ledger!
                      (fn [_]
                        {:ledger/bytes (js/Buffer.alloc 0)})
                      cli/require-repositories!
                      (fn [& _]
                        {"repo" {:path "repo"
                                 :absolute "/repo"
                                 :revision child-revision}})
                      cli/run-gate!
                      (fn [& _]
                        (reset! gate-ran? true)
                        ;; Model a non-cooperating writer that ignores the held lock.
                        (fs/writeFileSync file changed-ledger "utf8")
                        passed-result)]
          (is (thrown-with-msg?
               js/Error #"changed after held ledger validation"
               (cli/run-selected-gates!
                catalog test-catalog-identity
                {:only #{"repo"} :kinds #{:unit}})))
          (is @gate-ran?)
          (is (= changed-ledger (fs/readFileSync file "utf8")))
          (is (not (fs/existsSync lock-file))))))))

(deftest receipt-lines-require-one-complete-edn-form
  (is (= [{:a 1}] (cli/read-receipt-records! "{:a 1}\n")))
  (is (thrown-with-msg?
       js/Error #"exactly one EDN form"
       (cli/read-receipt-records! "{:a 1} {:b 2}\n")))
  (is (thrown-with-msg?
       js/Error #"exactly one EDN form"
       (cli/read-receipt-records! "{:a 1} trailing\n"))))

(deftest immutable-catalog-requires-one-complete-edn-form
  (let [contents "{:catalog/version 1}\n"
        calls (atom [])]
    (with-redefs [cli/git-capture!
                  (fn [args]
                    (swap! calls conj args)
                    (if (= "cat-file" (first args))
                      "commit\n"
                      (regular-blob-entry "config/quality-gates.edn")))
                  cli/git-buffer!
                  (fn [args]
                    (swap! calls conj args)
                    (js/Buffer.from contents "utf8"))]
      (let [bundle (cli/read-immutable-catalog-bundle!
                    reviewed-root-revision)]
        (is (= {:catalog/version 1} (:catalog bundle)))
        (is (= (cli/sha256 contents)
               (get-in bundle [:catalog-identity :catalog/sha256])))
        (is (= [["cat-file" "-t" reviewed-root-revision]
                ["ls-tree" "-z" reviewed-root-revision "--"
                 "config/quality-gates.edn"]
                ["show" (str reviewed-root-revision
                             ":config/quality-gates.edn")]]
               @calls))))
    (with-redefs [cli/git-capture!
                  (fn [args]
                    (if (= "cat-file" (first args))
                      "commit\n"
                      (regular-blob-entry "config/quality-gates.edn")))
                  cli/git-buffer!
                  (fn [_]
                    (js/Buffer.from
                     "{:catalog/version 1} {:forged true}\n" "utf8"))]
      (is (thrown-with-msg?
           js/Error #"exactly one EDN form"
           (cli/read-immutable-catalog-bundle!
            reviewed-root-revision))))))

(deftest immutable-git-bytes-are-hashed-raw-and-decoded-strictly
  (let [invalid-bytes (js/Buffer.from #js [255])]
    (is (= "a8100ae6aa1940d0b663bb31cd466142ebbdbd5187131b92d93818987832eb89"
           (cli/sha256 invalid-bytes)))
    (is (not= (cli/sha256 invalid-bytes)
              (cli/sha256 (js/Buffer.from "�" "utf8"))))
    (with-redefs [cli/git-capture!
                  (fn [args]
                    (if (= "cat-file" (first args))
                      "commit\n"
                      (regular-blob-entry
                       (if (= "ledger" (last args))
                         "ledger"
                         (last args)))))
                  cli/git-buffer! (fn [_] invalid-bytes)]
      (is (thrown-with-msg?
           js/Error #"Invalid UTF-8 in immutable Receipt River ledger"
           (cli/read-immutable-receipt-ledger!
            reviewed-root-revision)))
      (is (thrown-with-msg?
           js/Error #"Invalid UTF-8 in immutable quality gate catalog"
           (cli/read-immutable-catalog-bundle!
            reviewed-root-revision))))))

(deftest immutable-authority-files-must-be-regular-git-blobs
  (with-redefs [cli/git-capture!
                (fn [args]
                  (if (= "cat-file" (first args))
                    "commit\n"
                    (str "120000 blob " blob-revision "\t"
                         law/receipt-ledger-path "\u0000")))
                cli/git-buffer!
                (fn [_]
                  (throw (js/Error. "blob read must not be reached")))]
    (is (thrown-with-msg?
         js/Error #"non-executable regular Git blob"
         (cli/read-immutable-receipt-ledger!
          reviewed-root-revision)))))

(deftest reads-receipts-only-from-an-immutable-git-object
  (let [revision (apply str (repeat 40 "c"))
        receipt (cli/evidence-receipt
                 passed-result "2026-08-29T17:22:40Z" "test"
                 "nbb/node@test")
        contents (str (pr-str receipt) "\n")
        calls (atom [])]
    (with-redefs [cli/git-capture!
                  (fn [args]
                    (swap! calls conj args)
                    (if (= "cat-file" (first args))
                      "commit\n"
                      (regular-blob-entry law/receipt-ledger-path)))
                  cli/git-buffer!
                  (fn [args]
                    (swap! calls conj args)
                    (js/Buffer.from contents "utf8"))]
      (let [ledger (cli/read-immutable-receipt-ledger! revision)]
        (is (= [["cat-file" "-t" revision]
                ["ls-tree" "-z" revision "--" law/receipt-ledger-path]
                ["show" (str revision ":" law/receipt-ledger-path)]]
               @calls))
        (is (= {:ledger/path law/receipt-ledger-path
                :ledger/revision revision
                :ledger/sha256 (cli/sha256 contents)}
               (:ledger/identity ledger)))
        (is (= [receipt] (:ledger/records ledger)))
        (is (law/immutable-receipt-ledger? ledger))))
    (is (thrown-with-msg?
         js/Error #"full lowercase Git commit ID"
         (cli/read-immutable-receipt-ledger! "main")))))

(deftest receipt-river-head-preserves-base-bytes-and-whole-lines
  (let [base (js/Buffer.from "{:a 1}\n" "utf8")
        appended (js/Buffer.from "{:b 2}\n" "utf8")
        head (js/Buffer.concat #js [base appended])]
    (is (= [{:b 2}] (cli/appended-receipt-records! base head)))
    (is (= [] (cli/appended-receipt-records! base base)))
    (is (thrown-with-msg?
         js/Error #"does not preserve the base bytes as a prefix"
         (cli/appended-receipt-records!
          (js/Buffer.from "{:a 0}\n" "utf8") head)))
    (is (thrown-with-msg?
         js/Error #"must end with a newline"
         (cli/appended-receipt-records!
          base (js/Buffer.concat
                #js [base (js/Buffer.from "{:b 2}" "utf8")]))))
    (is (= [{:b 2}]
           (cli/appended-receipt-records!
            (js/Buffer.from "{:a 1}" "utf8")
            (js/Buffer.from "{:a 1}\n{:b 2}\n" "utf8"))))))

(deftest receipt-verification-rejects-invalid-appended-envelopes
  (let [base-record {:ts "2026-08-29T17:22:40Z"
                     :kind :decision
                     :repo "."
                     :origin "test"
                     :owner "test"
                     :dod "retain"
                     :pi "eta-mu"
                     :host "test"
                     :manifest ["test"]
                     :refs ["test"]}
        base-text (str (pr-str base-record) "\n")
        invalid-record (dissoc base-record :origin)
        head-text (str base-text (pr-str invalid-record) "\n")
        ledger (fn [revision text records]
                 {:ledger/identity
                  {:ledger/path law/receipt-ledger-path
                   :ledger/revision revision
                   :ledger/sha256 (cli/sha256
                                   (js/Buffer.from text "utf8"))}
                  :ledger/bytes (js/Buffer.from text "utf8")
                  :ledger/records records})]
    (with-redefs [cli/read-immutable-receipt-ledger!
                  (fn [revision]
                    (if (= revision child-revision)
                      (ledger revision base-text [base-record])
                      (ledger revision head-text
                              [base-record invalid-record])))]
      (is (thrown-with-msg?
           js/Error #"invalid receipt envelopes"
           (cli/verify-receipts!
            {:base child-revision :at reviewed-root-revision})))))
  (let [base (js/Buffer.from "" "utf8")
        head (js/Buffer.from "42\n" "utf8")]
    (is (= [42] (cli/appended-receipt-records! base head)))))

(defn with-historical-ledger-fixture [terminal-newline? run!]
  (with-receipt-fixture
    (fn [{:keys [fixture file] :as context}]
      (with-redefs [cli/root fixture
                    cli/receipt-file file]
        (cli/git-capture! ["init" "--quiet"])
        (let [current (cli/evidence-receipt
                       passed-result "2026-08-29T17:22:40Z" "test"
                       "nbb/node@test")
              historical (-> current
                             (dissoc :evidence/schema :evidence/adapter)
                             (assoc :manifest "historical" :refs "none"))
              base-text (str (pr-str historical)
                             (when terminal-newline? "\n"))
              commit! (fn [contents]
                        (fs/writeFileSync file contents)
                        (cli/git-capture! ["add" "--" law/receipt-ledger-path])
                        (cli/git-capture!
                         ["-c" "user.name=Receipt Test"
                          "-c" "user.email=receipt-test@example.invalid"
                          "-c" "commit.gpgsign=false"
                          "commit" "--quiet" "--allow-empty" "-m" "fixture"])
                        (str/trim
                         (cli/git-capture! ["rev-parse" "HEAD"])))
              base (commit! base-text)]
          (run! (assoc context
                       :current current :historical historical
                       :base-text base-text :base base :commit! commit!)))))))

(deftest receipt-verification-admits-bound-envelope-view-with-original-bytes
  (with-historical-ledger-fixture true
    (fn [{:keys [base base-text current commit!]}]
      (let [original (assoc current
                            :kind :decision
                            :origin "bound-envelope-target"
                            :manifest "exact, manifest contents"
                            :refs "exact, reference contents"
                            :dod ["first requirement" " second requirement "]
                            :pi :cephalon/character-memory)
            original-line (str (pr-str original) "\n")
            source-text (str base-text original-line)
            source (commit! source-text)
            correction (assoc current
                              :kind :correction
                              :origin "bound-envelope-correction"
                              :correction/entries
                              [{:receipt/line 2
                                :receipt/origin (:origin original)
                                :receipt/source-revision source
                                :receipt/source-sha256
                                (cli/sha256 (js/Buffer.from original-line "utf8"))
                                :envelope/corrected-fields
                                {:manifest ["exact, manifest contents"]
                                 :refs ["exact, reference contents"]
                                 :dod "first requirement;  second requirement "
                                 :pi "cephalon/character-memory"}}])
            head-text (str source-text (pr-str correction) "\n")
            head (commit! head-text)
            outcome (try
                      {:exit (cli/verify-receipts! {:base base :at head})}
                      (catch :default error
                        {:error (.-message error)}))]
        (is (not (law/receipt-envelope? original)))
        (is (law/receipt-envelope? correction))
        (is (str/starts-with? head-text source-text)
            "The source's original physical ledger bytes stay unchanged")
        (is (= {:exit 0} outcome)
            "An exact ancestor/line/origin/including-LF hash correction should admit the documentary view")))))

(defn with-correction-ledger-fixture [run!]
  (with-historical-ledger-fixture true
    (fn [{:keys [base-text current commit!] :as context}]
      (let [original (assoc current :dod ["exact, ημ objective" "second objective"])
            original-line (str (pr-str original) "\n")
            source-text (str base-text original-line)
            source (commit! source-text)
            entry {:receipt/line 2
                   :receipt/origin (:origin original)
                   :receipt/source-revision source
                   :receipt/source-sha256
                   (cli/sha256 (js/Buffer.from original-line "utf8"))
                   :envelope/corrected-fields
                   {:dod "exact, ημ objective; second objective"}}
            correction (assoc current :kind :correction
                              :origin "bound-correction" :correction/entries [entry])]
        (run! (assoc context :original original :original-line original-line
                     :source source :source-text source-text
                     :entry entry :correction correction))))))

(defn correction-head! [{:keys [commit! source-text correction]}]
  (commit! (str source-text (pr-str correction) "\n")))

(deftest correction-verification-discloses-raw-counts-and-provenance-deterministically
  (with-correction-ledger-fixture
    (fn [{:keys [base entry original file] :as context}]
      (let [head (correction-head! context)
            before (fs/readFileSync file)
            read! #(with-out-str (is (zero? (cli/verify-receipts! {:base base :at head}))))
            first-output (read!)]
        (is (= first-output (read!)))
        (is (str/includes? first-output ":total-receipts 3"))
        (is (str/includes? first-output ":appended-receipts 2"))
        (is (str/includes? first-output ":corrected-receipts 1"))
        (is (str/includes? first-output (pr-str (assoc entry :correction/line 3))))
        (is (.equals before (fs/readFileSync file)))
        (is (= original (second (:ledger/records (cli/read-immutable-receipt-ledger! head)))))))))

(deftest correction-verification-refuses-wrong-source-bindings
  (with-correction-ledger-fixture
    (fn [{:keys [base entry original-line] :as context}]
      (doseq [[field value message]
              [[:receipt/origin "other-origin" #"correction source origin"]
               [:receipt/line 1 #"correction source hash"]
               [:receipt/line 20 #"correction source line"]
               [:receipt/source-revision (apply str (repeat 40 "f"))
                #"correction source unavailable"]
               [:receipt/source-sha256 (apply str (repeat 64 "a"))
                #"correction source hash"]
               [:receipt/source-sha256
                (cli/sha256 (js/Buffer.from (subs original-line 0 (dec (count original-line))) "utf8"))
                #"correction source hash"]]]
        (let [head (correction-head!
                    (assoc-in context [:correction :correction/entries]
                              [(assoc entry field value)]))]
          (is (thrown-with-msg? js/Error message
                                (cli/verify-receipts! {:base base :at head}))))))))

(deftest correction-verification-refuses-available-nonancestor-source
  (with-correction-ledger-fixture
    (fn [{:keys [base source entry] :as context}]
      (let [tree (str/trim (cli/git-capture! ["rev-parse" (str source "^{tree}")]))
            unrelated (str/trim
                       (cli/git-capture!
                        ["-c" "user.name=Receipt Test"
                         "-c" "user.email=receipt-test@example.invalid"
                         "commit-tree" tree "-m" "unrelated source"]))
            head (correction-head!
                  (assoc-in context [:correction :correction/entries]
                            [(assoc entry :receipt/source-revision unrelated)]))]
        (is (thrown-with-msg? js/Error #"correction source is not an ancestor"
                              (cli/verify-receipts! {:base base :at head})))))))

(deftest correction-verification-refuses-reserialized-original-line
  (with-correction-ledger-fixture
    (fn [{:keys [base base-text original-line] :as context}]
      ;; Same parsed map, different physical bytes. Source binding is not EDN
      ;; equality or a digest of a normalized reserialization.
      (let [head (correction-head!
                  (assoc context :source-text
                         (str base-text " " original-line)))]
        (is (thrown-with-msg? js/Error #"correction target bytes differ"
                              (cli/verify-receipts! {:base base :at head})))))))

(deftest correction-verification-keeps-semantic-evidence-validation
  (with-correction-ledger-fixture
    (fn [{:keys [base base-text original commit! correction entry]}]
      (let [invalid (assoc-in original [:evidence/result :result/exit] 7)
            line (str (pr-str invalid) "\n")
            source-text (str base-text line)
            source (commit! source-text)
            correction (assoc correction :correction/entries
                              [(assoc entry :receipt/source-revision source
                                      :receipt/source-sha256
                                      (cli/sha256 (js/Buffer.from line "utf8")))])
            head (commit! (str source-text (pr-str correction) "\n"))]
        (doseq [trusted-base [base head]]
          (is (thrown-with-msg? js/Error #"invalid evidence receipts"
                                (cli/verify-receipts! {:base trusted-base :at head}))))))))

(deftest correction-verification-revalidates-source-binding-in-trusted-prefix
  (with-correction-ledger-fixture
    (fn [{:keys [file] :as context}]
      (let [head (correction-head!
                  (assoc-in context [:correction :correction/entries 0
                                     :receipt/source-sha256]
                            (apply str (repeat 64 "0"))))
            before (fs/readFileSync file)]
        (is (thrown-with-msg? js/Error #"correction source hash differs"
                              (cli/verify-receipts! {:base head :at head})))
        (is (.equals before (fs/readFileSync file)))))))

(deftest promotion-consumes-qualified-view-without-changing-originals
  (with-correction-ledger-fixture
    (fn [{:keys [base file] :as context}]
      (let [head (correction-head! context)
            before (fs/readFileSync file)
            catalog {:catalog/version 1 :catalog/repositories
                     {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}]
        (with-redefs [cli/read-immutable-catalog-bundle!
                      (fn [_] {:catalog catalog :catalog-identity test-catalog-identity})
                      cli/validate-catalog! identity
                      cli/gitlink-target! (fn [& _] child-revision)]
          (is (cli/promotion-ready-at! child-revision #{:repo/unit}
                                       [passed-result] base head)))
        (is (.equals before (fs/readFileSync file)))))))

(deftest promotion-retains-qualified-view-after-trusted-base-advances
  (with-correction-ledger-fixture
    (fn [{:keys [base file current commit!] :as context}]
      (let [corrected-head (correction-head! context)
            catalog {:catalog/version 1 :catalog/repositories
                     {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}]
        (with-redefs [cli/read-immutable-catalog-bundle!
                      (fn [_] {:catalog catalog :catalog-identity test-catalog-identity})
                      cli/validate-catalog! identity
                      cli/gitlink-target! (fn [& _] child-revision)]
          (is (cli/promotion-ready-at! child-revision #{:repo/unit}
                                       [passed-result] base corrected-head))
          (is (cli/promotion-ready-at! child-revision #{:repo/unit}
                                       [passed-result] corrected-head corrected-head))
          (let [ordinary (assoc current :kind :observation :origin "ordinary-after-correction")
                contents (str (fs/readFileSync file "utf8") (pr-str ordinary) "\n")
                next-head (commit! contents)]
            (is (cli/promotion-ready-at! child-revision #{:repo/unit}
                                         [passed-result] corrected-head next-head))
            (is (= contents (fs/readFileSync file "utf8")))))))))

(deftest held-correction-admission-rechecks-captured-head-before-gate
  (with-correction-ledger-fixture
    (fn [{:keys [file source-text correction]}]
      ;; Source has descended from the base and is now committed HEAD. Only the
      ;; documentary correction is in the uncommitted held extension.
      (fs/writeFileSync file (str source-text (pr-str correction) "\n"))
      (let [catalog {:catalog/repositories
                     {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}
            captured-git cli/git-capture!
            heads-read (atom 0)
            gate-ran? (atom false)
            before (fs/readFileSync file)]
        (with-redefs [cli/require-repositories! (fn [& _] {"repo" {}})
                      cli/git-capture!
                      (fn [args]
                        (if (and (= ["rev-parse" "HEAD"] args)
                                 (> (swap! heads-read inc) 1))
                          reviewed-root-revision
                          (captured-git args)))
                      cli/run-gate! (fn [& _] (reset! gate-ran? true) passed-result)]
          (is (thrown-with-msg? js/Error #"captured receipt HEAD changed"
                                (cli/run-selected-gates!
                                 catalog test-catalog-identity
                                 {:only #{"repo"} :kinds #{:unit}}))))
        (is (false? @gate-ran?))
        (is (.equals before (fs/readFileSync file)))))))

(deftest held-correction-admits-only-sources-in-captured-committed-ancestry
  (doseq [capture-old-head? [true false]]
    (with-correction-ledger-fixture
      (fn [{:keys [base file source-text correction]}]
        (let [held-text (str source-text (pr-str correction) "\n")
              captured-git cli/git-capture!
              gate-ran? (atom false)
              catalog {:catalog/repositories
                       {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}]
          (fs/writeFileSync file held-text)
          (with-redefs [cli/require-repositories! (fn [& _] {"repo" {}})
                        cli/git-capture! (fn [args]
                                           (if (and capture-old-head?
                                                    (= ["rev-parse" "HEAD"] args))
                                             base (captured-git args)))
                        cli/run-gate! (fn [& _] (reset! gate-ran? true) passed-result)]
            (if capture-old-head?
              (is (thrown-with-msg? js/Error #"correction source is not an ancestor"
                                    (cli/run-selected-gates!
                                     catalog test-catalog-identity
                                     {:only #{"repo"} :kinds #{:unit}})))
              (is (zero? (cli/run-selected-gates!
                          catalog test-catalog-identity
                          {:only #{"repo"} :kinds #{:unit}})))))
          (is (= (not capture-old-head?) @gate-ran?))
          (if capture-old-head?
            (is (= held-text (fs/readFileSync file "utf8")))
            (is (str/starts-with? (fs/readFileSync file "utf8") held-text))))))))

(deftest correction-verification-retains-absolute-physical-ordinals
  (with-correction-ledger-fixture
    (fn [{:keys [base base-text original-line commit! correction entry]}]
      (let [source-text (str base-text "\n" original-line)
            source (commit! source-text)
            correction (assoc correction :correction/entries
                              [(assoc entry :receipt/line 3 :receipt/source-revision source)])
            head (commit! (str source-text (pr-str correction) "\n"))
            output (with-out-str
                     (is (zero? (cli/verify-receipts! {:base base :at head}))))]
        (is (str/includes? output ":receipt/line 3"))
        (is (str/includes? output ":correction/line 4"))))))

(deftest correction-verification-rejects-source-without-final-lf
  (with-correction-ledger-fixture
    (fn [{:keys [base base-text original-line commit! correction entry]}]
      (let [without-lf (subs original-line 0 (dec (count original-line)))
            source (commit! (str base-text without-lf))
            correction (assoc correction :correction/entries
                              [(assoc entry :receipt/source-revision source
                                      :receipt/source-sha256
                                      (cli/sha256 (js/Buffer.from without-lf "utf8")))])
            head (commit! (str base-text original-line (pr-str correction) "\n"))]
        (is (thrown-with-msg? js/Error #"source line must include final LF"
                              (cli/verify-receipts! {:base base :at head})))))))

(deftest correction-verification-refuses-second-claim-across-trusted-prefix
  (with-correction-ledger-fixture
    (fn [{:keys [correction commit!] :as context}]
      (let [first-head (correction-head! context)
            before (:ledger/bytes (cli/read-immutable-receipt-ledger! first-head))
            second-head (commit! (str (.toString before "utf8")
                                     (pr-str (assoc correction :origin "second-claim")) "\n"))]
        (is (thrown-with-msg? js/Error #"duplicate target"
                              (cli/verify-receipts! {:base first-head :at second-head})))))))

(deftest frozen-pr27-suffix-retains-all-originals-and-exact-eleven-views
  (let [suffix (fs/readFileSync
                (path/join cli/root "test/fixtures/receipt-correction/pr27-suffix.edn"))
        ;; Blank prefix supplies ordinals only. No Git fact is inferred here;
        ;; the actual base/source/head command is separately retained evidence.
        bytes (js/Buffer.concat
               #js [(js/Buffer.from (apply str (repeat 259 "\n")) "utf8") suffix])
        raw-items (cli/physical-receipt-items! bytes)
        items (mapv #(select-keys % [:receipt/line :receipt/record]) raw-items)
        entries (cli/correction-entries items)
        by-line (into {} (map (juxt :receipt/line identity)) raw-items)
        bindings (into {}
                       (map (fn [entry]
                              (let [item (get by-line (:receipt/line entry))]
                                (is (= (:receipt/source-sha256 entry)
                                       (cli/sha256 (:receipt/bytes item))))
                                [[(:receipt/source-revision entry) (:receipt/line entry)
                                  (:receipt/source-sha256 entry)]
                                 (assoc (dissoc entry :envelope/corrected-fields)
                                        :binding/verified? true
                                        :receipt/record (:receipt/record item))])))
                       entries)
        result (law/receipt-correction-view items bindings)]
    (is (= "52e3dc0e2fa4103c03a688d5c747780ae8d1d0ecd58af3c854e4455e094e6eba"
           (cli/sha256 suffix)))
    (is (= 19 (count items)))
    (is (= 11 (count (remove law/receipt-envelope? (:receipt/originals result)))))
    (is (= [] (:receipt/errors result)))
    (is (= 11 (count (:receipt/corrections result))))
    (is (every? law/receipt-envelope? (:receipt/views result)))
    (is (= (mapv :receipt/record items) (:receipt/originals result)))
    (doseq [[original view] (map vector (:receipt/originals result) (:receipt/views result))]
      (is (= (dissoc original :manifest :refs :dod :pi)
             (dissoc view :manifest :refs :dod :pi))))))

(deftest receipt-verification-trusts-exact-historical-prefix-only
  (doseq [terminal-newline? [true false]]
    (with-historical-ledger-fixture terminal-newline?
      (fn [{:keys [base base-text historical current commit!]}]
        (is (not (law/receipt-envelope? historical)))
        (is (zero? (cli/verify-receipts! {:base base :at base})))
        (let [head-text (str base-text (when-not terminal-newline? "\n")
                             (pr-str current) "\n")
              head (commit! head-text)
              output (with-out-str
                       (is (zero? (cli/verify-receipts!
                                   {:base base :at head}))))]
          (is (str/includes? output ":total-receipts 2"))
          (is (str/includes? output ":appended-receipts 1"))
          (is (str/includes? output ":appended-evidence-receipts 1"))
          (is (str/includes? output ":legacy-evidence-receipts 1"))
          (is (.equals (js/Buffer.from base-text "utf8")
                       (:ledger/bytes
                        (cli/read-immutable-receipt-ledger! base)))))))))

(deftest receipt-verification-rejects-invalid-suffixes-after-history
  (with-historical-ledger-fixture true
    (fn [{:keys [base base-text historical current commit!]}]
      (doseq [[suffix message]
              [[(str (pr-str historical) "\n") #"invalid receipt envelopes"]
               ["42\n" #"invalid receipt envelopes"]
               [(str (pr-str (dissoc current :origin)) "\n")
                #"invalid receipt envelopes"]
               [(str (pr-str (assoc-in current [:evidence/result :result/exit] 1))
                     "\n") #"invalid evidence receipts"]
               ["{:kind\n" #"Invalid Receipt River EDN"]
               [(str (pr-str current) " 42\n") #"exactly one EDN form"]
               [(pr-str current) #"must end with a newline"]]]
        (let [head (commit! (str base-text suffix))]
          (is (thrown-with-msg?
               js/Error message
               (cli/verify-receipts! {:base base :at head}))))))))

(deftest receipt-verification-rejects-historical-rewrite-and-truncation
  (with-historical-ledger-fixture true
    (fn [{:keys [base base-text historical commit!]}]
      (doseq [head-text [""
                        (subs base-text 0 (dec (count base-text)))
                        (str (pr-str (assoc historical :owner "rewrite")) "\n")]]
        (let [head (commit! head-text)]
          (is (thrown-with-msg?
               js/Error #"does not preserve the base bytes as a prefix"
               (cli/verify-receipts! {:base base :at head}))))))))

(deftest evidence-append-preserves-history-and-validates-uncommitted-suffix
  (doseq [terminal-newline? [true false]]
    (with-historical-ledger-fixture terminal-newline?
      (fn [{:keys [base base-text file]}]
        ;; A second writer must accept the first writer's canonical suffix.
        (let [first-receipt (cli/append-evidence-receipt! passed-result)
              second-receipt (cli/append-evidence-receipt! passed-result)
              bytes (fs/readFileSync file)]
          (is (cli/buffer-prefix? (js/Buffer.from base-text "utf8") bytes))
          (is (= [first-receipt second-receipt]
                 (cli/appended-receipt-records!
                  (:ledger/bytes (cli/read-immutable-receipt-ledger! base))
                  bytes))))))))

(deftest selected-gates-reject-invalid-extensions-of-historical-ledger
  (with-historical-ledger-fixture true
    (fn [{:keys [base-text historical current file directory]}]
      (doseq [[held-text message]
              [["" #"does not preserve the committed ledger"]
               [(str (pr-str (assoc historical :owner "rewrite")) "\n")
                #"does not preserve the committed ledger"]
               [(str base-text (pr-str historical) "\n")
                #"invalid receipt envelopes"]
               [(str base-text
                     (pr-str (assoc-in current [:evidence/result :result/exit] 1))
                     "\n") #"invalid evidence receipts"]
               [(str base-text (pr-str current)) #"must end with a newline"]
               [(str base-text "{:kind\n") #"Invalid Receipt River EDN"]]]
        (fs/writeFileSync file held-text "utf8")
        (let [gate-ran? (atom false)
              catalog {:catalog/repositories
                       {"repo" {:repository/path "repo"
                                :repository/gates [local-gate]}}}]
          (with-redefs [cli/require-repositories! (fn [& _] {"repo" {}})
                        cli/run-gate! (fn [& _]
                                        (reset! gate-ran? true)
                                        passed-result)]
            (is (thrown-with-msg?
                 js/Error message
                 (cli/run-selected-gates!
                  catalog test-catalog-identity
                  {:only #{"repo"} :kinds #{:unit}}))))
          (is (false? @gate-ran?))
          (is (= held-text (fs/readFileSync file "utf8")))
          (is (not (fs/existsSync
                    (path/join directory ".receipts.edn.append.lock")))))))))

(deftest promotion-preserves-history-without-promoting-legacy-evidence
  (with-historical-ledger-fixture true
    (fn [{:keys [base base-text current commit!]}]
      (let [catalog {:catalog/version 1
                     :catalog/repositories
                     {"repo" {:repository/path "repo"
                              :repository/gates [local-gate]}}}]
        (with-redefs [cli/read-immutable-catalog-bundle!
                      (fn [_] {:catalog catalog
                               :catalog-identity test-catalog-identity})
                      cli/validate-catalog! identity
                      cli/gitlink-target! (fn [& _] child-revision)]
          (is (false? (cli/promotion-ready-at!
                       child-revision #{:repo/unit} [passed-result] base base)))
          (let [head (commit! (str base-text (pr-str current) "\n"))]
            (is (cli/promotion-ready-at!
                 child-revision #{:repo/unit} [passed-result] base head)))
          (let [head (commit!
                      (str base-text (pr-str current) "\n"
                           (pr-str (assoc-in current
                                             [:evidence/result :result/exit] 1))
                           "\n"))]
            (is (thrown-with-msg?
                 js/Error #"invalid evidence receipts"
                 (cli/promotion-ready-at!
                  child-revision #{:repo/unit} [passed-result] base head)))))))))

(deftest promotion-authority-binds-current-head-catalog-ledger-and-gitlink
  (let [revision reviewed-root-revision
        ledger {:ledger/identity
                {:ledger/path law/receipt-ledger-path
                 :ledger/revision revision
                 :ledger/sha256 (apply str (repeat 64 "d"))}
                :ledger/bytes (js/Buffer.alloc 0)
                :ledger/records
                [(cli/evidence-receipt
                  passed-result "2026-08-29T17:22:40Z" "test"
                  "nbb/node@test")]}
        reads (atom [])
        catalog {:catalog/version 1
                 :catalog/repositories
                 {"repo" {:repository/path "repo"
                          :repository/gates [local-gate]}}}]
    (with-redefs [cli/current-root-state!
                  (fn []
                    (swap! reads conj :root-state)
                    {:root/revision revision :root/status ""})
                  cli/read-immutable-catalog-bundle!
                  (fn [at]
                    (swap! reads conj [:catalog at])
                    {:catalog catalog
                     :catalog-identity test-catalog-identity})
                  cli/validate-catalog! identity
                  cli/read-immutable-receipt-ledger!
                  (fn [at]
                    (swap! reads conj [:ledger at])
                    ledger)
                  cli/appended-receipt-records!
                  (fn [& _]
                    (swap! reads conj :append-history)
                    [])
                  cli/require-result-gitlinks!
                  (fn [actual-catalog gate-ids results at]
                    (swap! reads conj [:gitlinks actual-catalog gate-ids
                                       results at])
                    true)]
      (is (cli/promotion-ready-at!
           child-revision #{:repo/unit} [passed-result]
           trusted-base-revision revision))
      (is (= [:root-state
              [:catalog revision]
              [:ledger trusted-base-revision]
              [:ledger revision]
              :append-history
              [:gitlinks catalog #{:repo/unit} [passed-result] revision]
              :root-state]
             @reads)))))

(deftest promotion-authority-rejects-noncurrent-or-dirty-root
  (with-redefs [cli/current-root-state!
                (fn [] {:root/revision (apply str (repeat 40 "d"))
                        :root/status ""})]
    (is (thrown-with-msg?
         js/Error #"not the current HEAD"
         (cli/promotion-ready-at!
          child-revision #{:repo/unit} [passed-result]
          trusted-base-revision
          reviewed-root-revision))))
  (with-redefs [cli/current-root-state!
                (fn [] {:root/revision reviewed-root-revision
                        :root/status " M config/quality-gates.edn\n"})]
    (is (thrown-with-msg?
         js/Error #"tracked or submodule changes"
         (cli/promotion-ready-at!
          child-revision #{:repo/unit} [passed-result]
          trusted-base-revision
          reviewed-root-revision)))))

(deftest promotion-authority-rechecks-root-after-immutable-reads
  (let [states (atom [{:root/revision reviewed-root-revision
                       :root/status ""}
                      {:root/revision reviewed-root-revision
                       :root/status " M repo\n"}])
        catalog {:catalog/version 1
                 :catalog/repositories
                 {"repo" {:repository/path "repo"
                          :repository/gates [local-gate]}}}
        ledger {:ledger/identity
                {:ledger/path law/receipt-ledger-path
                 :ledger/revision reviewed-root-revision
                 :ledger/sha256 (apply str (repeat 64 "d"))}
                :ledger/bytes (js/Buffer.alloc 0)
                :ledger/records
                [(cli/evidence-receipt
                  passed-result "2026-08-29T17:22:40Z" "test"
                  "nbb/node@test")]}]
    (with-redefs [cli/current-root-state!
                  (fn []
                    (let [state (first @states)]
                      (swap! states subvec 1)
                      state))
                  cli/read-immutable-catalog-bundle!
                  (fn [_] {:catalog catalog
                           :catalog-identity test-catalog-identity})
                  cli/validate-catalog! identity
                  cli/read-immutable-receipt-ledger! (fn [_] ledger)
                  cli/appended-receipt-records! (fn [& _] [])
                  cli/require-result-gitlinks! (fn [& _] true)]
      (is (thrown-with-msg?
           js/Error #"tracked or submodule changes"
           (cli/promotion-ready-at!
            child-revision #{:repo/unit} [passed-result]
            trusted-base-revision
            reviewed-root-revision)))
      (is (empty? @states)))))

(deftest gitlink-target-is-exact-and-unambiguous
  (with-redefs [cli/git-capture!
                (fn [args]
                  (is (= ["ls-tree" "-z" reviewed-root-revision "--" "repo"]
                         args))
                  (str "160000 commit " child-revision "\trepo\u0000"))]
    (is (= child-revision
           (cli/gitlink-target! reviewed-root-revision "repo"))))
  (with-redefs [cli/git-capture!
                (fn [_]
                  (str "160000 commit " sha256-child-revision
                       "\trepo\u0000"))]
    (is (= sha256-child-revision
           (cli/gitlink-target! reviewed-root-revision "repo"))))
  (with-redefs [cli/git-capture!
                (fn [_]
                  (str "100644 blob " child-revision "\trepo\u0000"))]
    (is (thrown-with-msg?
         js/Error #"not the expected gitlink"
         (cli/gitlink-target! reviewed-root-revision "repo")))))

(deftest validates-the-checked-in-catalog
  (let [{:keys [catalog catalog-identity]} (cli/read-catalog-bundle)]
    (is (law/valid-catalog? catalog))
    (is (identical? catalog (cli/validate-catalog! catalog)))
    (is (contains? (cli/actionable-submodule-paths) "knoxx"))
    (is (not (contains? (cli/actionable-submodule-paths) ".agents")))
    (is (= "config/quality-gates.edn" (:catalog/path catalog-identity)))
    (is (re-matches #"[0-9a-f]{64}" (:catalog/sha256 catalog-identity)))
    (is (thrown-with-msg?
         js/Error
         #"catalog/repository-not-actionable-submodule"
         (cli/validate-catalog!
          (assoc-in catalog
                    [:catalog/repositories "typo"]
                    {:repository/path "typo" :repository/gates []}))))
    (is (thrown-with-msg?
         js/Error
         #"catalog/repositories"
         (cli/validate-catalog!
          (assoc catalog :catalog/repositories 42))))
    (is (thrown-with-msg? js/Error #"Repositories have no mapped gates"
                          (cli/list-gates! catalog
                                           {:only #{"missing"}
                                            :kinds law/gate-kinds})))
    (is (thrown-with-msg? js/Error #"No mapped gates match"
                          (cli/list-gates! catalog
                                           {:only #{"katamorph"}
                                            :kinds #{:security}})))))

(deftest knoxx-gates-remain-under-knoxx-ownership
  (let [gates (get-in (cli/read-catalog)
                      [:catalog/repositories "knoxx" :repository/gates])]
    (is (seq gates))
    (is (every? #(= :workflow-only (:gate/execution %)) gates))
    (is (every? #(not (contains? % :gate/command)) gates))))

(deftest local-process-results-do-not-hide-failures
  (with-redefs [cli/spawn-result (fn [& _] #js {:status 0})]
    (is (= :passed
           (:result/outcome
            (cli/local-result "/repo" {:gate/id :repo/unit
                                        :gate/command ["test"]})))))
  (with-redefs [cli/spawn-result (fn [& _] #js {:status 7})]
    (is (= {:gate/id :repo/unit
            :result/outcome :failed
            :result/exit 7}
           (cli/local-result "/repo" {:gate/id :repo/unit
                                      :gate/command ["test"]}))))
  (with-redefs [cli/spawn-result
                (fn [& _] #js {:status nil
                               :error (js/Error. "spawn ENOENT")})]
    (is (= {:gate/id :repo/unit
            :result/outcome :unavailable
            :result/reason "spawn ENOENT"}
           (cli/local-result "/repo" {:gate/id :repo/unit
                                      :gate/command ["test"]})))))

(deftest dirty-or-moving-checkouts-cannot-produce-revision-evidence
  (let [catalog {:catalog/repositories {"repo" {:repository/gates []}}}]
    (with-redefs [workspace/inventory
                  (fn [] [{:path "repo"
                           :actionable true
                           :exists true
                           :initialized true
                           :dirty true
                           :git-errors {}
                           :head "abc123"}])
                  workspace/execution-paths!
                  (fn [repositories]
                    (is (empty? repositories))
                    [])]
      (is (= {:path "repo"
              :unavailable-reason "Repository checkout is dirty"
              :revision "abc123"}
             (get (cli/require-repositories! catalog #{"repo"}) "repo")))))
  (let [spawned? (atom false)]
    (with-redefs [cli/execution-path-unavailable-reason (constantly nil)
                  workspace/git-state
                  (fn [_] {:initialized true
                           :head "revision-b"
                           :dirty false
                           :git-errors {}})
                  cli/spawn-result
                  (fn [& _]
                    (reset! spawned? true)
                    #js {:status 0})]
      (is (= :unavailable
             (:result/outcome
              (cli/run-gate! {:path "repo"
                              :absolute "/repo"
                              :revision "revision-a"}
                             local-gate
                             test-catalog-identity))))
      (is (false? @spawned?)))))

(deftest execution-path-identity-is-rechecked
  (let [inventory {:path "repo"
                   :actionable true
                   :source-type "git-submodule"
                   :ownership "independent-repository"
                   :device 7
                   :inode 11}
        repository {:absolute "/repo" :inventory inventory}]
    (with-redefs [workspace/execution-paths!
                  (fn [repositories]
                    (is (= [inventory] repositories))
                    [{:repo inventory :absolute "/repo"}])]
      (is (nil? (cli/execution-path-unavailable-reason repository))))
    (with-redefs [workspace/execution-paths!
                  (fn [_] [{:repo inventory :absolute "/replacement"}])]
      (is (= "Repository execution path changed after inventory"
             (cli/execution-path-unavailable-reason repository))))
    (with-redefs [workspace/execution-paths!
                  (fn [_]
                    (throw (js/Error. "Executable source identity changed")))]
      (is (= (str "Repository execution path could not be reverified: "
                  "Executable source identity changed")
             (cli/execution-path-unavailable-reason repository))))))

(deftest local-gates-recheck-execution-path-around-every-spawn
  (let [path-results (atom [nil "Repository execution path changed after inventory"])
        git-rechecks (atom 0)
        spawn-count (atom 0)]
    (with-redefs [cli/execution-path-unavailable-reason
                  (fn [_]
                    (let [result (first @path-results)]
                      (swap! path-results subvec 1)
                      result))
                  workspace/git-state
                  (fn [_]
                    (swap! git-rechecks inc)
                    {:initialized true
                     :head "revision-a"
                     :dirty false
                     :git-errors {}})
                  cli/spawn-result
                  (fn [& _]
                    (swap! spawn-count inc)
                    #js {:status 0})]
      (let [result (cli/run-gate! {:path "repo"
                                   :absolute "/repo"
                                   :revision "revision-a"}
                                  local-gate
                                  test-catalog-identity)]
        (is (empty? @path-results))
        (is (= 1 @git-rechecks))
        (is (= 1 @spawn-count))
        (is (= :unavailable (:result/outcome result)))
        (is (= :passed (:result/attempt-outcome result)))
        (is (= 0 (:result/attempt-exit result)))
        (is (= (str "Evidence rejected after gate execution: "
                    "Repository execution path changed after inventory")
               (:result/reason result)))
        (is (nil? (:result/revision result)))))))

(deftest classifies-every-local-checkout-recheck-branch
  (let [repository {:revision "revision-a"}]
    (is (= "Repository checkout became uninitialized"
           (cli/local-unavailable-reason
            repository
            {:initialized false :head "revision-a" :dirty false :git-errors {}})))
    (is (= "Repository Git state could not be reverified"
           (cli/local-unavailable-reason
            repository
            {:initialized true :head "revision-a" :dirty false
             :git-errors {:head "failed"}})))
    (is (= "Repository checkout became dirty"
           (cli/local-unavailable-reason
            repository
            {:initialized true :head "revision-a" :dirty true :git-errors {}})))
    (is (= "Repository checkout cleanliness could not be reverified"
           (cli/local-unavailable-reason
            repository
            {:initialized true :head "revision-a" :dirty nil :git-errors {}})))
    (is (= "Repository revision changed after inventory"
           (cli/local-unavailable-reason
            repository
            {:initialized true :head "revision-b" :dirty false :git-errors {}})))
    (is (nil? (cli/local-unavailable-reason
               repository
               {:initialized true :head "revision-a" :dirty false :git-errors {}})))))

(deftest local-gates-recheck-after-spawn-and-retain-provenance
  (let [states (atom [{:initialized true
                       :head "revision-a"
                       :dirty false
                       :git-errors {}}
                      {:initialized true
                       :head "revision-b"
                       :dirty false
                       :git-errors {}}])
        spawn-count (atom 0)]
    (with-redefs [cli/execution-path-unavailable-reason (constantly nil)
                  workspace/git-state
                  (fn [_]
                    (let [state (first @states)]
                      (swap! states subvec 1)
                      state))
                  cli/spawn-result
                  (fn [cwd command]
                    (is (= "/repo" cwd))
                    (is (= ["test" "--exact"] command))
                    (swap! spawn-count inc)
                    #js {:status 0})]
      (let [result (cli/run-gate! {:path "repo"
                                   :absolute "/repo"
                                   :revision "revision-a"}
                                  local-gate
                                  test-catalog-identity)]
        (is (= 1 @spawn-count))
        (is (= :unavailable (:result/outcome result)))
        (is (= :passed (:result/attempt-outcome result)))
        (is (= "revision-b" (:result/observed-revision result)))
        (is (nil? (:result/revision result)))
        (is (= ["test" "--exact"] (:result/command result)))
        (is (= test-catalog-identity (:result/catalog result)))
        (is (= {:source/path "repo/package.json"
                :source/repository "repo"
                :source/revision "revision-a"}
               (:result/source result)))))))

(deftest stable-local-gates-bind-the-verified-revision
  (with-redefs [cli/execution-path-unavailable-reason (constantly nil)
                workspace/git-state
                (fn [_] {:initialized true
                         :head "revision-a"
                         :dirty false
                         :git-errors {}})
                cli/spawn-result (fn [& _] #js {:status 0})]
    (let [result (cli/run-gate! {:path "repo"
                                 :absolute "/repo"
                                 :revision "revision-a"}
                                local-gate
                                test-catalog-identity)]
      (is (= :passed (:result/outcome result)))
      (is (= "revision-a" (:result/revision result)))
      (is (= ["test" "--exact"] (:result/command result))))))

(deftest coverage-zero-exit-remains-unavailable-without-report-attestation
  (let [coverage-gate (assoc local-gate
                             :gate/id :repo/coverage
                             :gate/kind :coverage)]
    (with-redefs [cli/execution-path-unavailable-reason (constantly nil)
                  workspace/git-state
                  (fn [_] {:initialized true
                           :head child-revision
                           :dirty false
                           :git-errors {}})
                  cli/spawn-result (fn [& _] #js {:status 0})]
      (let [result (cli/run-gate! {:path "repo"
                                   :absolute "/repo"
                                   :revision child-revision}
                                  coverage-gate
                                  test-catalog-identity)]
        (is (= :unavailable (:result/outcome result)))
        (is (= cli/coverage-artifact-unavailable-reason
               (:result/reason result)))
        (is (= :passed (:result/attempt-outcome result)))
        (is (= 0 (:result/attempt-exit result)))
        (is (not (contains? result :result/exit)))
        (is (law/valid-result? result))
        (is (= 3 (cli/result-exit result)))))))

(deftest missing-checkouts-and-external-hosts-remain-non-passes
  (is (= :unavailable
         (:result/outcome
          (cli/run-gate! {:path "repo"
                          :unavailable-reason "not initialized"}
                         {:gate/id :repo/unit
                          :gate/kind :unit
                          :gate/execution :local
                          :gate/command ["test"]
                          :gate/source "repo/package.json"}
                         test-catalog-identity))))
  (is (= :blocked
         (:result/outcome
          (cli/run-gate! {:path "repo" :revision "revision-a"}
                         {:gate/id :repo/live
                          :gate/kind :live-smoke
                          :gate/execution :external
                          :gate/source "repo/.github/workflows/live.yml"
                          :gate/reason "needs host"}
                         test-catalog-identity)))))

(deftest process-exit-contract-keeps-non-passes-nonzero
  (is (zero? (cli/result-exit {:result/outcome :passed})))
  (is (= 1 (cli/result-exit {:result/outcome :failed})))
  (is (= 3 (cli/result-exit {:result/outcome :unavailable})))
  (is (= 4 (cli/result-exit {:result/outcome :blocked})))
  (is (zero? (cli/result-exit {:result/outcome :not-applicable}))))

(deftest receipt-stream-declarations-cannot-pass-as-ordinary-appends
  (let [ordinary {:ts "2026-10-08T01:20:00Z"
                  :kind :decision :origin "stream-shape-fixture"
                  :owner "test" :dod "authenticate source before admission"
                  :pi "receipt-stream" :host "isolated test"
                  :manifest [] :refs []}
        candidates [(assoc ordinary :kind :receipt-stream-import)
                    (assoc ordinary :receipt/stream {})
                    (assoc ordinary :kind :receipt-stream-import
                           :receipt/stream nil)
                    (assoc ordinary :kind :receipt-stream-import
                           :receipt/stream {})]]
    (doseq [candidate candidates]
      (let [git-calls (atom 0)
            head (js/Buffer.from (str (pr-str candidate) "\n") "utf8")]
        (is (law/receipt-envelope? candidate)
            "Ordinary envelope validity cannot authenticate an import")
        (with-redefs [cli/git-capture! (fn [& _]
                                        (swap! git-calls inc)
                                        (throw (js/Error. "unexpected Git call")))
                      cli/git-buffer! (fn [& _]
                                       (swap! git-calls inc)
                                       (throw (js/Error. "unexpected Git call")))]
          (is (thrown-with-msg?
               js/Error #"Receipt source stream admission refused"
               (cli/admit-receipt-extension!
                (js/Buffer.alloc 0) head reviewed-root-revision)))
          (is (zero? @git-calls)
              "Malformed declarations refuse before source Git reads"))))))

(def frozen-stream-common "1069d4c9bcba29ef55e6c72c7c597de101f0520b")
(def frozen-stream-parent "b3fffe3bc29cca09b6ff70b41768160b288a12ba")
(def frozen-stream-head "bdac1ed6470c98128ff367e757132797a39ac436")

(def stream-fixture-record
  {:ts "2026-10-08T01:20:00Z" :kind :observation
   :origin "source-stream-fixture" :owner "test"
   :dod "preserve source occurrences" :pi "receipt-stream"
   :host "isolated Git fixture" :manifest [] :refs []})

(defn receipt-line-bytes [record]
  (js/Buffer.from (str (pr-str record) "\n") "utf8"))

(defn stream-descriptor [base head base-bytes head-bytes]
  (let [delta (.subarray head-bytes (.-length base-bytes))]
    {:source/base base :source/head head :source/path law/receipt-ledger-path
     :source/ledger-sha256 (cli/sha256 head-bytes)
     :source/ledger-bytes (.-length head-bytes)
     :source/ledger-records (count (cli/physical-receipt-items! head-bytes))
     :source/delta-sha256 (cli/sha256 delta)
     :source/delta-bytes (.-length delta)
     :source/delta-records (count (cli/physical-receipt-items! delta))}))

(defn with-frozen-stream-fixture [run!]
  ;; Borrow immutable objects through an alternate in this temporary repository.
  ;; All new trees, commits, index writes and refs belong only to the fixture.
  (let [objects (path/resolve cli/root
                              (str/trim (cli/git-capture! ["rev-parse" "--git-path" "objects"])))
        common (:ledger/bytes (cli/read-immutable-receipt-ledger! frozen-stream-common))
        parent (:ledger/bytes (cli/read-immutable-receipt-ledger! frozen-stream-parent))
        source (:ledger/bytes (cli/read-immutable-receipt-ledger! frozen-stream-head))]
    (with-receipt-fixture
      (fn [{:keys [fixture file] :as context}]
        (with-redefs [cli/root fixture cli/receipt-file file]
          (cli/git-capture! ["init" "--quiet"])
          (fs/writeFileSync (path/join fixture ".git/objects/info/alternates")
                            (str objects "\n") "utf8")
          (let [commit! (fn [bytes parents]
                          (fs/writeFileSync file bytes)
                          (cli/git-capture! ["add" "--" law/receipt-ledger-path])
                          (let [tree (str/trim (cli/git-capture! ["write-tree"]))
                                head (str/trim
                                      (cli/git-capture!
                                       (into ["-c" "user.name=Receipt Test"
                                              "-c" "user.email=receipt-test@example.invalid"
                                              "commit-tree" tree "-m" "isolated stream fixture"]
                                             (mapcat #(vector "-p" %) parents))))]
                            (cli/git-capture! ["update-ref" "HEAD" head])
                            head))
                descriptor (stream-descriptor frozen-stream-common frozen-stream-head common source)
                import (assoc stream-fixture-record :kind :receipt-stream-import
                              :receipt/stream descriptor)
                head-bytes (js/Buffer.concat #js [parent (receipt-line-bytes import)])
                head (commit! head-bytes [frozen-stream-parent frozen-stream-head])]
            (run! (merge context {:common common :parent parent :source source
                                 :descriptor descriptor :import import :commit! commit!
                                 :head head :head-bytes head-bytes}))))))))

(deftest frozen-source-stream-retains-original-coordinates-and-every-receipt
  (with-frozen-stream-fixture
    (fn [{:keys [common parent source head head-bytes]}]
      (is (= "01ef747305b0446523c00caf39833da0737241bfa3698bc093a13e56dea9d177" (cli/sha256 common)))
      (is (= "3e217a5d9e2e8f5e4f43a8692bff57535b9966111ab1d59e8f67d8258f6f5161" (cli/sha256 parent)))
      (is (= "e0657dbd24dd5327371e6a144c4fcb4001b3e1146940a6c269fa746570db035a" (cli/sha256 source)))
      (is (thrown-with-msg? js/Error #"does not preserve the base bytes"
                            (cli/admit-receipt-extension! parent source frozen-stream-head)))
      (let [admission (cli/admit-receipt-extension! parent head-bytes head)
            source-items (vec (drop 259 (cli/physical-receipt-items! source)))
            occurrences (:ledger/occurrences admission)
            imported (filterv #(= :imported (:receipt/stream-role %)) occurrences)]
        (is (= {:ledger/canonical-receipts 265 :ledger/imported-receipts 19
                :ledger/combined-receipts 284}
               (select-keys admission [:ledger/canonical-receipts :ledger/imported-receipts
                                       :ledger/combined-receipts])))
        (is (= 284 (count (:ledger/originals admission))))
        (is (= 284 (count (:ledger/views admission))))
        (is (= 11 (count (:receipt/corrections admission))))
        (is (= (mapv :receipt/record source-items) (mapv :receipt/original imported)))
        (is (= (vec (range 260 279)) (mapv :receipt/line imported)))
        (is (= (mapv #(cli/sha256 (:receipt/bytes %)) source-items)
               (mapv :receipt/sha256 imported)))
        (is (= (vec (range 267 278)) (mapv :receipt/line (:receipt/corrections admission))))
        (is (every? #(= 278 (:correction/line %)) (:receipt/corrections admission)))
        (is (= 284 (count (set (map :receipt/identity occurrences)))))
        (is (.equals head-bytes (:ledger/bytes (cli/read-immutable-receipt-ledger! head))))))))

(deftest source-stream-is-reauthenticated-after-base-advancement-and-ordinary-append
  (with-frozen-stream-fixture
    (fn [{:keys [head head-bytes import commit!]}]
      (let [first-view (cli/admit-receipt-extension! head-bytes head-bytes head)
            appended (js/Buffer.concat #js [head-bytes (receipt-line-bytes stream-fixture-record)])
            next-head (commit! appended [head])
            next-view (cli/admit-receipt-extension! head-bytes appended next-head)
            second-import (js/Buffer.concat #js [appended (receipt-line-bytes import)])
            second-head (commit! second-import [next-head])]
        (is (= 284 (count (:ledger/views first-view))))
        (is (= 11 (count (:receipt/corrections first-view))))
        (is (= 285 (:ledger/combined-receipts next-view)))
        (is (= 266 (:ledger/canonical-receipts next-view)))
        (is (= 19 (:ledger/imported-receipts next-view)))
        (is (= (filterv #(= :imported (:receipt/stream-role %)) (:ledger/occurrences first-view))
               (filterv #(= :imported (:receipt/stream-role %)) (:ledger/occurrences next-view))))
        (doseq [base [appended second-import]]
          (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                                (cli/admit-receipt-extension! base second-import second-head))))))))

(deftest source-stream-refuses-tampered-descriptors-and-foreign-objects
  (with-frozen-stream-fixture
    (fn [{:keys [parent import head commit!]}]
      (doseq [[field value]
              [[:source/base frozen-stream-parent]
               [:source/head (apply str (repeat 40 "0"))]
               [:source/path "./.ημ/receipts.edn"]
               [:source/ledger-sha256 (apply str (repeat 64 "0"))]
               [:source/delta-sha256 (apply str (repeat 64 "0"))]
               [:source/ledger-bytes 537421] [:source/ledger-records 277]
               [:source/delta-bytes 60486] [:source/delta-records 18]
               [:source/repository "foreign"]]]
        (let [bytes (js/Buffer.concat
                     #js [parent (receipt-line-bytes (assoc-in import [:receipt/stream field] value))])
              candidate (commit! bytes [head])]
          (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                                (cli/admit-receipt-extension! parent bytes candidate))
              (str "tampered descriptor " field))))
      (let [tree (str/trim (cli/git-capture! ["rev-parse" (str frozen-stream-head "^{tree}")]))
            foreign (str/trim (cli/git-capture!
                              ["-c" "user.name=Receipt Test" "-c" "user.email=receipt-test@example.invalid"
                               "commit-tree" tree "-m" "unrelated object"]))
            bytes (js/Buffer.concat #js [parent (receipt-line-bytes
                                                 (assoc-in import [:receipt/stream :source/head] foreign))])
            candidate (commit! bytes [head])]
        (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                              (cli/admit-receipt-extension! parent bytes candidate)))))))

(deftest source-stream-refuses-cross-stream-overlap-and-nested-imports
  (with-frozen-stream-fixture
    (fn [{:keys [common parent source import head commit!]}]
      (let [row (:receipt/bytes (nth (cli/physical-receipt-items! source) 259))
            overlap (js/Buffer.concat #js [parent row (receipt-line-bytes import)])
            overlap-head (commit! overlap [head])]
        (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                              (cli/admit-receipt-extension! parent overlap overlap-head))))
      (let [nested-bytes (js/Buffer.concat #js [source (receipt-line-bytes import)])
            nested-head (commit! nested-bytes [frozen-stream-head])
            nested-import (assoc import :receipt/stream
                                 (stream-descriptor frozen-stream-common nested-head common nested-bytes))
            bytes (js/Buffer.concat #js [parent (receipt-line-bytes nested-import)])
            candidate (commit! bytes [head nested-head])]
        (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                              (cli/admit-receipt-extension! parent bytes candidate)))))))

(deftest source-stream-preserves-equal-occurrences-and-blank-physical-ordinals
  (with-frozen-stream-fixture
    (fn [{:keys [common parent import head commit!]}]
      (let [line (receipt-line-bytes stream-fixture-record)
            blank (js/Buffer.from "\n" "utf8")
            source (js/Buffer.concat #js [common blank line blank line])
            source-head (commit! source [frozen-stream-common])
            descriptor (stream-descriptor frozen-stream-common source-head common source)
            bytes (js/Buffer.concat #js [parent (receipt-line-bytes (assoc import :receipt/stream descriptor))])
            candidate (commit! bytes [head source-head])
            admission (cli/admit-receipt-extension! parent bytes candidate)
            occurrences (filterv #(= :imported (:receipt/stream-role %)) (:ledger/occurrences admission))]
        (is (= 267 (:ledger/combined-receipts admission)))
        (is (= 2 (:ledger/imported-receipts admission)))
        (is (= [261 263] (mapv :receipt/line occurrences)))
        (is (= [(cli/sha256 line) (cli/sha256 line)] (mapv :receipt/sha256 occurrences)))
        (is (= 2 (count (set (map :receipt/identity occurrences)))))))))

(deftest source-stream-refuses-nonlinear-unavailable-and-unframed-source-bytes
  (with-frozen-stream-fixture
    (fn [{:keys [common parent source import head commit!]}]
      (doseq [bytes [(.subarray source 0 (dec (.-length source)))
                    (js/Buffer.concat #js [common (js/Buffer.from #js [255 10])])
                    (js/Buffer.concat #js [common
                                          (:receipt/bytes (nth (cli/physical-receipt-items! source) 260))
                                          (.subarray source (.-length common))])]]
        (let [source-head (commit! bytes [frozen-stream-head])
              candidate-bytes (js/Buffer.concat
                               #js [parent (receipt-line-bytes
                                             (assoc-in import [:receipt/stream :source/head] source-head))])
              candidate (commit! candidate-bytes [head source-head])]
          (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                                (cli/admit-receipt-extension! parent candidate-bytes candidate)))))
      (let [new-source (js/Buffer.concat #js [source (receipt-line-bytes stream-fixture-record)])
            new-source-head (commit! new-source [frozen-stream-head])
            candidate-bytes (js/Buffer.concat
                             #js [parent (receipt-line-bytes
                                           (assoc import :receipt/stream
                                                  (stream-descriptor frozen-stream-common new-source-head common new-source)))])]
        ;; The object exists, but the immutable held anchor cannot reach it.
        (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                              (cli/admit-receipt-extension! parent candidate-bytes head)))))))

(deftest imported-evidence-cannot-become-valid-by-entering-another-stream
  (with-frozen-stream-fixture
    (fn [{:keys [common parent import head commit!]}]
      (let [invalid (assoc stream-fixture-record :origin law/evidence-receipt-origin
                           :kind :test-run :evidence/schema 2 :evidence/adapter "fixture"
                           :evidence/result (assoc passed-result :result/exit 9))
            source (js/Buffer.concat #js [common (receipt-line-bytes invalid)])
            source-head (commit! source [frozen-stream-common])
            bytes (js/Buffer.concat #js [parent (receipt-line-bytes
                                                (assoc import :receipt/stream
                                                       (stream-descriptor frozen-stream-common source-head common source)))])
            candidate (commit! bytes [head source-head])]
        (is (law/receipt-envelope? invalid))
        (is (false? (law/evidence-receipt? invalid)))
        (doseq [base [parent bytes]]
          (is (thrown-with-msg? js/Error #"invalid evidence receipts"
                                (cli/admit-receipt-extension! base bytes candidate))))))))

(deftest held-foreign-import-executes-no-gate-and-leaves-raw-ledger-unchanged
  (with-frozen-stream-fixture
    (fn [{:keys [file directory parent import head commit!]}]
      (let [source (js/Buffer.concat #js [parent (receipt-line-bytes stream-fixture-record)])
            source-head (commit! source [head])
            held (js/Buffer.concat #js [parent (receipt-line-bytes
                                               (assoc import :receipt/stream
                                                      (stream-descriptor frozen-stream-parent source-head parent source)))])
            gate-count (atom 0)
            catalog {:catalog/repositories
                     {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}]
        (cli/git-capture! ["update-ref" "HEAD" frozen-stream-parent])
        (fs/writeFileSync file held)
        (with-redefs [cli/require-repositories!
                      (fn [& _] {"repo" {:path "repo" :absolute "/repo" :revision child-revision}})
                      cli/run-gate! (fn [& _] (swap! gate-count inc) passed-result)]
          (is (thrown-with-msg? js/Error #"Receipt source stream admission refused"
                                (cli/run-selected-gates! catalog test-catalog-identity
                                                         {:only #{"repo"} :kinds #{:unit}})))
          (is (zero? @gate-count))
          (is (.equals held (fs/readFileSync file)))
          (is (not (fs/existsSync (path/join directory ".receipts.edn.append.lock")))))))))

(deftest verifier-and-promotion-consume-the-same-authenticated-stream-view
  (with-frozen-stream-fixture
    (fn [{:keys [head head-bytes file]}]
      (let [seen (atom nil)
            output (with-out-str (is (zero? (cli/verify-receipts!
                                            {:base frozen-stream-parent :at head}))))
            catalog {:catalog/version 1 :catalog/repositories
                     {"repo" {:repository/path "repo" :repository/gates [local-gate]}}}]
        (doseq [part [":total-receipts 284" ":canonical-receipts 265"
                      ":imported-receipts 19" ":corrected-receipts 11"]]
          (is (str/includes? output part) (str "verifier explicit count " part)))
        (with-redefs [cli/read-immutable-catalog-bundle!
                      (fn [_] {:catalog catalog :catalog-identity test-catalog-identity})
                      cli/validate-catalog! identity
                      law/promotion-evidence-consistent?
                      (fn [_ _ _ _ _ ledger] (reset! seen ledger) false)]
          ;; Capture the actual replaceable consumer seam; returning false avoids
          ;; inferring promotion from historical observations in the frozen source.
          (is (false? (cli/promotion-ready-at! child-revision #{:repo/unit}
                                               [passed-result] frozen-stream-parent head)))
          (is (= 284 (count (:ledger/records @seen))))
          (is (= 284 (count (:ledger/original-records @seen))))
          (is (= 11 (count (:ledger/correction-provenance @seen))))
          (is (.equals head-bytes (fs/readFileSync file))))))))

(defmethod test/report [::test/default :end-run-tests] [summary]
  (set! (.-exitCode js/process) (if (test/successful? summary) 0 1)))

(test/run-tests 'evidence-cli-test)
