;; SPDX-License-Identifier: GPL-3.0-or-later
(ns evidence-cli-test
  (:require [cljs.test :as test :refer [deftest is]]
            [clojure.string :as str]
            [evidence :as cli]
            [eta-mu.receipt-river.law.receipt :as native-rr]
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


;; Immutable F40 producer bytes, not regenerated or imported into this ledger.
;; Source7f02c7f3b987741eefebe6d35fdf064e5310b927, original lines198/199.
(def original-f40-observations
  [   {:line 198 :length 2553 :sha256 "198b312258607be9607a78c9e2ac67dce4eb634696dbc1bd8f334c393322a23a"
    :base64 "ezpldmVudC9pZCAjdXVpZCAiYzg5ZTk1NmEtZTNjOC00YmVkLTkxZGItOWVhNTI3ZmZlMDAwIiwgOmV2ZW50L3R5cGUgOnJlY2VpcHQtcml2ZXIvcmVjZWlwdC1yZWNvcmRlZCwgOmV2ZW50L3JlY29yZGVkLWF0ICIyMDI2LTEwLTE1VDE1OjU1OjUxLjE3NloiLCA6ZXZlbnQvc2NoZW1hIHs6aWQgOmV0YS1tdS5yZWNlaXB0LXJpdmVyL3JlY2VpcHQtcmVjb3JkZWQsIDp2ZXJzaW9uIDF9LCA6ZXZlbnQvcHJvZHVjZXIgezpldGEtbXUvdmVyc2lvbiAiMS4xLjEiLCA6cGFja2FnZS9uYW1lICJAZXRhLW11L3JlY2VpcHQtcml2ZXIiLCA6cGFja2FnZS92ZXJzaW9uICIwLjEuMCIsIDpjb21tYW5kICJldGEtbXUgcmVjZWlwdCBhcHBlbmQifSwgOmV2ZW50L3N1YmplY3QgezpyZXBvc2l0b3J5L3BhdGggIi9ob21lL2Vyci8uY29kZXgvd29ya3RyZWVzL2tub3h4LWZvcmstcmVqZWN0aW9uLXBsYW4vZm9yZXNpZ2h0In0sIDpldmVudC9wYXlsb2FkIHs6cGkgImV0YS1tdSIsIDpyZWZzICJub25lIiwgOmRvZCAicmVjZWlwdC1yaXZlciIsIDptYW5pZmVzdCAibm9uZSIsIDpub3RlICJQbGFubmluZyBvbmx5OiBzb3VyY2UyN2QyMDdiYmQ0YTc5ODJmYmE1ZjMwNGU0MWEyNmI3ZTUzODNlNjUwIG92ZXIgYWNjZXB0ZWQgcGVyc29uYWwgRm9yZXNpZ2h0IG1haW40NWUzM2Y5M2E1NjgwZTRjNzVhM2I2ODNmOTg3MGNlZDgyYTlmMThjIGNyZWF0ZXMgc2VwYXJhdGUgcHJvdmlzaW9uYWwgUDEvMyByZWplY3Rpb24tdHJhbnNwb3J0IHNpYmxpbmcgYTFjZGVhNTUtMTVmZS00YjQ5LTk5MGEtNDZlZDdmMTlhMWE0IHVuZGVyIGZvcmstcmV2aWV3LXNldHVwLCBleHBsaWNpdGx5ICszIGRpc2NvdmVyeS4gRXhhY3QgcGFyZW50IGJ5dGVzIGFuZCBpbmNvbWluZy9QMS8zIG1ldGFkYXRhIHJlbWFpbiB1bmNoYW5nZWQuIEV4aXN0aW5nIGFjdGlvbmFiaWxpdHkgYWEzOTc3MGQgaW5fcHJvZ3Jlc3MvUDEvMyByZW1haW5zIGluIGl0cyBzZXBhcmF0ZSBzb3VyY2UgbGFuZTsgbm8gdW5hZG9wdGVkIGFjdGlvbmFiaWxpdHkgb3IgQ01TL0YzOSBjYXJkL2V2ZW50IHN1ZmZpeCBpbXBvcnRlZC4gRnJlc2ggY2Fub25pY2FsIHBhcmVudCBhbmQgYm90aCBhY2NlcHRlZC1zb3VyY2UvZmFtaWx5IHJlamVjdGlvbiBkZWR1cCByZWFkcyBwcmVjZWRlZCBuYXRpdmUgVVVJRHY0IGNyZWF0ZSwgb25lIGNvbW1lbnQgYW5kIGxhd2Z1bCBpbmNvbWluZy1hY2NlcHRlZC1icmVha2Rvd247IGZvdXIgZ2VudWluZSBuYXRpdmUgZXZlbnRzIHByZXNlcnZlIHRoZSBjb21wbGV0ZTE0NTM1Mi1ieXRlIGFjY2VwdGVkIGV2ZW50IHByZWZpeC4gU291cmNlIGNvbW1pdCBvd25zIG9ubHkgbmV3IGNhcmQgYW5kIG5hdGl2ZSBldmVudCBsZWRnZXIuIENvbXBsZXRlNTA4IGFjY2VwdGVkIG9yaWdpbmFsIGJsb2IgaW5wdXRzIGNoZWNrZWQsNTA3IHVuY2hhbmdlZCBhbmQgb25seSBvd25pbmcgZXZlbnQgZmlsZSBjaGFuZ2VkLiBFeGFjdDExNzM1LWJ5dGUgYm9keSBTSEE4ZDNlYzYzYTViOWM0NDRiNzMyZDgzMGM0YzMyNzMyMzgwYzRjODUzOWZiYzdkZTAxMmI0YWIwMTA0ZmFhNjI1IGNhcnJpZXMgZml2ZSBmdXR1cmUgRVRBIHBhdGhzLCBjYXVzYWwgYWN0dWFsIGd1YXJkL2hvc3QgUkVELUdSRUVOIGFuZCBpbmplY3RlZCBmdWxsIGNvbGxlY3Rvci9wdWJsaXNoZXIgY29udHJvbHM7IG5hdGl2ZSBzY29wZWQgcXVhbGlmaWNhdGlvbiByZW1haW5zIGxhdGVyIGFuZCBubyBwb3NpdGl2ZSBhbnN3ZXIgaXMgcHJvbWlzZWQuIFByZXNlcnZlIGNhbm9uaWNhbCByZWplY3Rpb24vYXBwcm92YWwvd2l0aGRyYXdhbC9wcmluY2lwYWwvcGlucywgYWN0dWFsIEFwcC9ncmFudC9lbnZpcm9ubWVudCB1bmtub3ducyBhbmQgdW5hbnN3ZXJlZCBzY29wZWQgQ29kZXggcmVxdWVzdHM7IG5vIGdlbmVyaWMgYXBwcm92YWwvcm91bmQvc2V0dGxlbWVudCBjcmVkaXQuIE5hdGl2ZSBkaWZmIGNoZWNrMCBhbmQgZXhhY3QyIHNvdXJjZSBwYXRocy9jbGVhbiBjb21taXQgcXVhbGlmaWVkOyBubyBhcHAvd29ya2Zsb3cgaW1wbGVtZW50YXRpb24gb3IgaW1wbGVtZW50YXRpb24gdGVzdHMsIHJlYWRpbmVzcywgUFIvcmV2aWV3ZXIvbW9kZWwvcHJvdmlkZXIvQ0kvQXBwL2FjY291bnQvc2VjcmV0L3NldHRpbmdzL3J1bnRpbWUvZGVwbG95bWVudCBlZmZlY3QuIENvcnJlY3RlZCBpbml0aWFsIEYzOSBhc3NvY2lhdGlvbiBhZnRlciBmdWxsIG5hdGl2ZSBHRVQ6IGFjdHVhbCBGMzkgaXMgc2VwYXJhdGUgQ01TIGN1dG92ZXIgcGxhbiwgbm90IHRoaXMgYWN0aW9uYWJpbGl0eSBmYW1pbHkuIFB1YmxpYyBwbGFubmluZyBwdWJsaWNhdGlvbiBIT0xEIGZvciByb290IGV4YWN0IHNvdXJjZS9wcm92ZW5hbmNlIHJlYWQuIENhbmRpZGF0ZTAsIG5vIHNwb3JlLiIsIDp0cyAiMjAyNi0xMC0xNVQxNTo1NTo1MS4xNzZaIiwgOmhvc3QgImxvY2FsIiwgOmtpbmQgOm9ic2VydmF0aW9uLCA6b3JpZ2luICJldGEtbXUiLCA6cmVwbyAiL2hvbWUvZXJyLy5jb2RleC93b3JrdHJlZXMva25veHgtZm9yay1yZWplY3Rpb24tcGxhbi9mb3Jlc2lnaHQiLCA6b3duZXIgInJlY2VpcHQtcml2ZXIifX0K"}
   {:line 199 :length 2133 :sha256 "b6baedd2bc9b34a07f99c81195cfe4364ca8eed3ac61449d158ca3658c096dab"
    :base64 "IzpldmVudHs6aWQgI3V1aWQgIjlhZmZjMjEwLTk2YWEtNGRlYi05NzY4LWYzNmFjMjA4ZTYxZiIsIDp0eXBlIDpyZWNlaXB0LXJpdmVyL3JlY2VpcHQtcmVjb3JkZWQsIDpyZWNvcmRlZC1hdCAiMjAyNi0xMC0xNVQxNjoxMjozNS4wODdaIiwgOnNjaGVtYSB7OmlkIDpldGEtbXUucmVjZWlwdC1yaXZlci9yZWNlaXB0LXJlY29yZGVkLCA6dmVyc2lvbiAxfSwgOnByb2R1Y2VyIHs6ZXRhLW11L3ZlcnNpb24gIjEuMS4xIiwgOnBhY2thZ2UvbmFtZSAiQGV0YS1tdS9yZWNlaXB0LXJpdmVyIiwgOnBhY2thZ2UvdmVyc2lvbiAiMC4xLjAiLCA6Y29tbWFuZCAiUmVjZWlwdCBSaXZlciBwYWNrYWdlIEFQSSAvIG93bmVkIHBsYW5uaW5nIGNsYXJpZmljYXRpb24iLCA6YWN0b3IgImNvZGV4L2Ntc19mcm9udGVuZF9jYXBfcHJvYmUifSwgOnN1YmplY3QgIzpyZXBvc2l0b3J5ezpwYXRoICIvaG9tZS9lcnIvLmNvZGV4L3dvcmt0cmVlcy9rbm94eC1mb3JrLXJlamVjdGlvbi1wbGFuL2ZvcmVzaWdodCJ9LCA6cGF5bG9hZCB7OnBpICJldGEtbXUiLCA6cmVmcyAibm9uZSIsIDpkb2QgInJlY2VpcHQtcml2ZXIiLCA6bWFuaWZlc3QgIm5vbmUiLCA6bm90ZSAiUGxhbm5pbmcgZmFjdHVhbCBjbGFyaWZpY2F0aW9uIG9ubHk6IHNvdXJjZTc3NTljYzcwOWJiMzgyOTllZTEwMzZlMmQxNGVmMzUzZGYyY2NkZjYgZm9sbG93cyBwcmVzZXJ2ZWQyN2QyMDdiYiBhbmQ1M2I1OGYxYiBoaXN0b3J5IG92ZXIgYWNjZXB0ZWQ0NWUzMy4gRXhhY3QgU0RLMTcgTm9uLWdvYWxzIHdvcmRpbmcgbm93IHByZXNlcnZlcyBhY3R1YWwgcGlubmVkIHdpdGhkcmF3YWwgcnVsZXMvbmF0aXZlIGlkZW50aXRpZXMgd2l0aG91dCBpbnZlbnRlZCBVc2VyIGV2aWRlbmNlLCBhY3RvciBzdWJzdGl0dXRpb24gb3IgY29waWVkIEJvdCB0ZXh0LiBObyBwb2xpY3kvcHJpbmNpcGFsL3BpbiBjaGFuZ2Ugb3IgU0RLMTcgYWRtaXNzaW9uIGlzIGNsYWltZWQuIEV4aXN0aW5nIHJlamVjdGlvbiBzdG9yeSBhMWNkZWE1NSByZW1haW5zIGJyZWFrZG93bi9QMS9wcm92aXNpb25hbDMgdW5kZXIgdW5jaGFuZ2VkIG9wZW4gZm9yay1yZXZpZXctc2V0dXA7IGZpdmUgZnV0dXJlIEVUQSBwYXRocyBhbmQgKzMgZGlzY292ZXJ5IHVuY2hhbmdlZC4gT25lIG5hdGl2ZSBvd25lciBjb21tZW50IGFuZCBldmVudCBwcmVzZXJ2ZSBvbGQgYm9keS9jb21tZW50IGNvbnRlbnQgZXhjZXB0IGV4YWN0IGFwcHJvdmVkIHNlbnRlbmNlIGFuZCBuYXRpdmUgd3JpdGUtaWQuIFByaXZhdGUgZ3VhcmQgZXhwZWN0ZWQgYSB0aGlyZCBuYXRpdmUgY29tbWVudCBzZWN0aW9uIGFuZCBmYWlsZWQgYmVmb3JlIHN0YWdlL2NvbW1pdC9wcm92ZW5hbmNlOyBhY3R1YWwgUmhlb3MgY29tYmluZXMgZXhhY3Qgb2xkIGNvbW1lbnQgcGx1cyBuZXcgbm90ZSBzZXBhcmF0ZWQgYnkgdHdvIG5ld2xpbmVzLCBpbmRlcGVuZGVudGx5IGNoZWNrZWQgYmVmb3JlIGNvbnRpbnVhdGlvbi4gT3JpZ2luYWwgZmFpbGVkIHByZWRpY2F0ZSByZXRhaW5lZCBhbmQgbm8gY29tbWVudCByZXBlYXRlZC4gRXhhY3QyIHNvdXJjZSBwYXRocywgc3RhZ2VkIGRpZmZjaGVjazAgYW5kIGNsZWFuIHNvdXJjZSBjb21taXQgcXVhbGlmaWVkLiBUaGlzIGdlbnVpbmUgcGFja2FnZS1BUEkgcmVjZWlwdCB1c2VzIGV4cGxpY2l0IGNhbm9uaWNhbCAuzrfOvC9yZWNlaXB0cy5lZG4gc3RvcmFnZSBiZWNhdXNlIHRoZSBlYXJsaWVyIG5hdGl2ZSBDTEkgaGFyZGNvZGVzIHJvb3Q7IHByZXNlcnZlIGFsbCBvbGQgcmVjZWlwdCBieXRlcyBhbmQgZXZlbnQgaWRlbnRpdGllcy4gUHVibGljIHBsYW5uaW5nIHB1YmxpY2F0aW9uIHJlbWFpbnMgSE9MRDsgbm8gaW1wbGVtZW50YXRpb24vcmVhZGluZXNzL1BSL3Jldmlld2VyL21vZGVsL0FwcC9jcmVkZW50aWFsL2FjY291bnQvcnVudGltZS9kZXBsb3ltZW50IGVmZmVjdHMuIENhbmRpZGF0ZTAsIG5vIHNwb3JlLiIsIDp0cyAiMjAyNi0xMC0xNVQxNjoxMjozNS4wODdaIiwgOmhvc3QgImxvY2FsIiwgOmtpbmQgOm9ic2VydmF0aW9uLCA6b3JpZ2luICJldGEtbXUiLCA6cmVwbyAiL2hvbWUvZXJyLy5jb2RleC93b3JrdHJlZXMva25veHgtZm9yay1yZWplY3Rpb24tcGxhbi9mb3Jlc2lnaHQiLCA6b3duZXIgInJlY2VpcHQtcml2ZXIifX0K"}])

(defn original-observation-bytes []
  (mapv (fn [{:keys [length sha256 base64]}]
          (let [bytes (js/Buffer.from base64 "base64")]
            (is (= length (.-length bytes)))
            (is (= sha256 (cli/sha256 bytes)))
            bytes))
        original-f40-observations))

(deftest original-native-observations-extend-the-real-historical-prefix
  (let [ancestor "45e33f93a5680e4c75a3b683f9870ced82a9f18c"
        base-bytes (cli/git-regular-blob-bytes!
                    ancestor law/receipt-ledger-path "F40 accepted prefix")
        original-bytes (original-observation-bytes)
        original-records (mapv #(cli/read-single-edn!
                                (cli/decode-utf8! % "original event")
                                "original event") original-bytes)
        history (cli/read-receipt-records! (.toString base-bytes "utf8"))
        head-bytes (js/Buffer.concat (clj->js (into [base-bytes] original-bytes)))]
    (is (= 308287 (.-length base-bytes)))
    (is (= "bc8e88c3fd153e6342409bbe6e053747232ad8c101c7862008756d876ba0a185"
           (cli/sha256 base-bytes)))
    (is (= 197 (count history)))
    (is (= 11 (count (remove law/receipt-envelope? history))))
    (is (= "e83c483f6df4b49d0d9058acf7d52708265e50a0b7cb73277da1c16f68b5083c"
           (cli/sha256 head-bytes)))
    (doseq [record original-records]
      (is (= [] (native-rr/record-errors record)))
      (is (uuid? (:event/id record)))
      (is (false? (law/receipt-envelope? record)))
      (is (false? (law/evidence-receipt? record))))
    (with-receipt-fixture
      (fn [{:keys [fixture file]}]
        (with-redefs [cli/root fixture cli/receipt-file file]
          (cli/git-capture! ["init" "--quiet"])
          (cli/git-capture! ["update-index" "--add" "--cacheinfo"
                             "160000,7c7c62343fea0241ac545e37f14e57ab344bfa30,receipt-river"])
          (let [commit! (fn [bytes]
                          (fs/writeFileSync file bytes)
                          (cli/git-capture! ["add" "--" law/receipt-ledger-path])
                          (cli/git-capture! ["-c" "user.name=Receipt Test"
                                             "-c" "user.email=receipt-test@example.invalid"
                                             "-c" "commit.gpgsign=false" "commit" "--quiet"
                                             "-m" "Original-byte observation fixture"])
                          (str/trim (cli/git-capture! ["rev-parse" "HEAD"])))
                base (commit! base-bytes)
                head (commit! head-bytes)]
            (is (= 0 (cli/verify-receipts! {:base base :at head})))))))))

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

(defmethod test/report [::test/default :end-run-tests] [summary]
  (set! (.-exitCode js/process) (if (test/successful? summary) 0 1)))

(test/run-tests 'evidence-cli-test)
