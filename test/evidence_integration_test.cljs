;; SPDX-License-Identifier: GPL-3.0-or-later
(ns evidence-integration-test
  (:require [cljs.test :as test :refer [deftest is]]
            [evidence :as cli]
            [foresight.evidence :as law]
            [clojure.string :as str]
            ["fs" :as fs]
            ["os" :as os]
            ["path" :as path]))

(defn text-bytes [s] (js/Buffer.from s "utf8"))
(defn legacy [s] {:segment/legacy? true :segment/bytes (text-bytes s)})
(defn segment [s] {:segment/path (str "segment-" (cli/sha256 (text-bytes s)))
                   :segment/sha256 (cli/sha256 (text-bytes s))
                   :segment/records (cli/read-receipt-records! s)})

(deftest anchored-source-physical-rows-must-survive-exactly
  (let [original (text-bytes "{:n 1}\n{:n 2}\n")]
    (is (true? (cli/require-anchored-source-rows!
                original [(legacy "{:n 1}\n{:n 2}\n")])))
    (is (true? (cli/require-anchored-source-rows!
                original [(legacy "{:n 1}\n") (segment "{:n 2}\n")])))
    (doseq [segments [[(legacy "{:n 1}\n")]
                      [(legacy "{:n 1}\n") (segment "{:n 2} \n")]
                      [(legacy "{:n 1}\n") (segment "{:n 2}")]]]
      (is (thrown? js/Error (cli/require-anchored-source-rows! original segments))))))

(deftest anchored-row-occurrences-cannot-collapse
  (is (thrown? js/Error
               (cli/require-anchored-source-rows!
                (text-bytes "{:n 1}\n{:n 1}\n") [(legacy "{:n 1}\n")])))
  (is (true? (cli/require-anchored-source-rows!
              (text-bytes "{:n 1}\n{:n 1}\n")
              [(legacy "{:n 1}\n") (segment "{:n 1}\n")]))))


(def anchored-rev (apply str (repeat 40 "a")))
(def head-rev (apply str (repeat 40 "b")))
(def base-rev (apply str (repeat 40 "c")))
(def raw-record {:n 1})
(def corrected-record {:n 1 :corrected true})
(def new-record {:n 2})
(def imported-record {:n 3})
(def raw-line (str (pr-str raw-record) "\n"))
(def new-line (str (pr-str new-record) "\n"))
(defn ledger-for [legacy-text segments records]
  {:ledger/records records :ledger/segments (into [(legacy legacy-text)] segments)})

(deftest projects-before-checking-and-retains-imports
  (let [head (ledger-for raw-line [(segment new-line)] [raw-record new-record])
        old (ledger-for "" [] [])
        admitted (atom nil)
        source-admission {:ledger/views [corrected-record imported-record]
                          :ledger/occurrences [{:receipt/stream-role :imported
                                                :receipt/sha256 (cli/sha256 (text-bytes "{:n 3}\n"))
                                                :receipt/original imported-record}]
                          :receipt/corrections [{:receipt/line 1}]}]
    (with-redefs [cli/legacy-source-binding! (fn [& _] {:source/bytes (text-bytes raw-line)
                                                         :source/revision anchored-rev})
                  cli/admit-receipt-extension! (fn [base candidate anchor]
                                                 (is (.equals base candidate))
                                                 (is (= anchored-rev anchor))
                                                 source-admission)
                  cli/ledger-extension-records! (fn [& _] [raw-record new-record])
                  cli/require-valid-receipt-records! (fn [records]
                                                       (reset! admitted records)
                                                       {:receipt/total (count records)})]
      (let [result (cli/admit-segmented-receipt-extension! old head base-rev head-rev)]
        (is (= [corrected-record new-record] @admitted))
        (is (= [corrected-record new-record imported-record]
               (get-in result [:ledger/records])))
        (is (= 1 (get-in result [:ledger/imported-receipts])))
        (is (= 3 (count (:ledger/occurrences result))))
        (is (= 2 (:ledger/appended-receipts result)))
        (is (= [{:receipt/line 1}]
               (get-in result [:ledger/correction-provenance])))))))

(deftest declarations-and-overlapping-imports-fail-closed
  (let [declaration {:kind :receipt-stream-import :receipt/stream {}}
        head (ledger-for "" [(segment new-line)] [declaration])
        old (ledger-for "" [] [])]
    (with-redefs [cli/legacy-source-binding! (fn [& _] nil)
                  cli/ledger-extension-records! (fn [& _] [declaration])]
      (is (thrown-with-msg? js/Error #"Unbound"
                            (cli/admit-segmented-receipt-extension! old head base-rev head-rev)))))
  (let [head (ledger-for raw-line [(segment new-line)] [raw-record new-record])]
    (with-redefs [cli/legacy-source-binding! (fn [& _] {:source/bytes (text-bytes raw-line)
                                                         :source/revision anchored-rev})
                  cli/ledger-extension-records! (fn [& _] [new-record])
                  cli/admit-receipt-extension! (fn [& _]
                                                 {:ledger/views [raw-record imported-record]
                                                  :ledger/occurrences [{:receipt/stream-role :imported
                                                                        :receipt/sha256 (cli/sha256 (text-bytes new-line))
                                                                        :receipt/original imported-record}]})]
      (is (thrown-with-msg? js/Error #"overlaps"
                            (cli/admit-segmented-receipt-extension!
                             (ledger-for "" [] []) head base-rev head-rev))))))

(deftest committed-binding-cannot-change-or-disappear
  (doseq [new-config [nil {:config/bytes (text-bytes "changed") }]]
    (with-redefs [cli/read-legacy-source-config! (fn [revision]
                                                   (if (= revision base-rev)
                                                     {:config/bytes (text-bytes "original")}
                                                     new-config))]
      (is (thrown-with-msg? js/Error #"changed or disappeared"
                            (cli/legacy-source-binding! base-rev head-rev))))))


(deftest legacy-binding-config-is-closed
  (let [digest (apply str (repeat 64 "a"))
        good {:receipt-legacy-source/revision anchored-rev
              :receipt-legacy-source/sha256 digest}]
    (doseq [value [(assoc good :extra true)
                   (assoc good :receipt-legacy-source/revision "not-a-commit")
                   (assoc good :receipt-legacy-source/sha256 "bad")
                   (dissoc good :receipt-legacy-source/sha256)]]
      (with-redefs [cli/git-capture! (fn [& _] "one-entry")
                    cli/git-regular-blob-bytes! (fn [& _] (text-bytes (pr-str value)))]
        (is (thrown-with-msg? js/Error #"Invalid closed"
                              (cli/read-legacy-source-config! head-rev)))))
    (with-redefs [cli/git-capture! (fn [& _] "one-entry")
                  cli/git-regular-blob-bytes! (fn [& _] (text-bytes (pr-str good)))]
      (is (= good (:config/value (cli/read-legacy-source-config! head-rev)))))
    (with-redefs [cli/git-capture! (fn [& _] "")]
      (is (nil? (cli/read-legacy-source-config! head-rev))))))

(deftest binding-requires-ancestor-and-digest
  (let [source (text-bytes raw-line)
        config {:config/bytes (text-bytes "binding")
                :config/value {:receipt-legacy-source/revision anchored-rev
                               :receipt-legacy-source/sha256 (cli/sha256 source)}}]
    (with-redefs [cli/read-legacy-source-config! (fn [_] config)
                  cli/git-capture! (fn [& _] "")
                  cli/read-immutable-receipt-ledger! (fn [_] {:ledger/bytes source})]
      (is (= anchored-rev (:source/revision (cli/legacy-source-binding! base-rev head-rev)))))
    (with-redefs [cli/read-legacy-source-config! (fn [_] config)
                  cli/git-capture! (fn [& _] (throw (js/Error. "not ancestor")))]
      (is (thrown-with-msg? js/Error #"not an ancestor"
                            (cli/legacy-source-binding! base-rev head-rev))))
    (with-redefs [cli/read-legacy-source-config! (fn [_] config)
                  cli/git-capture! (fn [& _] "")
                  cli/read-immutable-receipt-ledger! (fn [_] {:ledger/bytes (text-bytes "altered")})]
      (is (thrown-with-msg? js/Error #"digest differs"
                            (cli/legacy-source-binding! base-rev head-rev))))))


(def ordinary-record
  {:ts "2026-10-08T01:20:00Z" :kind :decision :origin "fixture-base"
   :owner "test" :dod "test migration" :pi "receipt" :host "fixture"
   :manifest [] :refs []})

(defn git-commit! []
  (cli/git-capture! ["add" "-A"])
  (cli/git-capture! ["-c" "user.name=Receipt Test" "-c" "user.email=test@example.invalid"
                     "-c" "commit.gpgsign=false" "commit" "--quiet" "--allow-empty" "-m" "fixture"])
  (str/trim (cli/git-capture! ["rev-parse" "HEAD"])))

(defn with-real-migration [include-uncorrected? run!]
  (let [dir (fs/mkdtempSync (path/join (os/tmpdir) "foresight-admission-"))
        ledger-dir (path/join dir ".ημ")
        ledger-file (path/join ledger-dir "receipts.edn")
        segments (path/join ledger-dir "receipts")
        config-dir (path/join dir "config")
        config-file (path/join config-dir "receipt-legacy-source.edn")]
    (fs/mkdirSync segments #js {:recursive true})
    (fs/mkdirSync config-dir)
    (with-redefs [cli/root dir cli/receipt-file ledger-file]
      (try
        (cli/git-capture! ["init" "--quiet"])
        (let [base-line (str (pr-str ordinary-record) "\n")
              original (assoc ordinary-record :origin "bad-source" :manifest "historical")
              original-line (str (pr-str original) "\n")
              uncorrected (assoc original :origin "uncorrected-source")
              extra-line (when include-uncorrected? (str (pr-str uncorrected) "\n"))]
          (fs/writeFileSync (path/join segments "README.md") "segments\n")
          (fs/writeFileSync ledger-file base-line)
          (let [base (git-commit!)
                _ (fs/writeFileSync ledger-file (str base-line original-line))
                source (git-commit!)
                correction (assoc ordinary-record :origin "source-correction" :kind :correction
                                  :correction/entries
                                  [{:receipt/line 2 :receipt/origin "bad-source"
                                    :receipt/source-revision source
                                    :receipt/source-sha256 (cli/sha256 (text-bytes original-line))
                                    :envelope/corrected-fields {:manifest ["historical"]}}])
                correction-line (str (pr-str correction) "\n")
                source-text (str base-line original-line correction-line extra-line)]
            (fs/writeFileSync ledger-file source-text)
            (let [anchor (git-commit!)
                  config {:receipt-legacy-source/revision anchor
                          :receipt-legacy-source/sha256 (cli/sha256 (text-bytes source-text))}
                  config-text (str (pr-str config) "\n")]
              (fs/writeFileSync config-file config-text)
              (fs/writeFileSync ledger-file base-line)
              (fs/writeFileSync (path/join segments "2026-10-08T012001000Z-original.edn") original-line)
              (fs/writeFileSync (path/join segments "2026-10-08T012002000Z-correction.edn") correction-line)
              (when extra-line
                (fs/writeFileSync (path/join segments "2026-10-08T012003000Z-uncorrected.edn") extra-line))
              (let [head (git-commit!)]
                (run! {:base base :head head :anchor anchor :dir dir :ledger-file ledger-file
                       :segments segments :config-file config-file :config-text config-text
                       :original original :original-line original-line})))))
        (finally (fs/rmSync dir #js {:recursive true :force true}))))))

(deftest committed-anchor-migrates-corrected-physical-row
  (with-real-migration false
    (fn [{:keys [base head anchor original original-line segments]}]
      (is (false? (law/receipt-envelope? original)))
      (is (= anchor (get-in (cli/read-legacy-source-config! head)
                            [:config/value :receipt-legacy-source/revision])))
      (is (= 0 (cli/verify-receipts! {:base base :at head})))
      (let [view (cli/admit-segmented-receipt-extension!
                  (cli/read-immutable-merged-ledger! base)
                  (cli/read-immutable-merged-ledger! head) base head)
            corrected (some #(when (= "bad-source" (:origin %)) %) (:ledger/records view))]
        (is (= ["historical"] (:manifest corrected)))
        (is (= 3 (:ledger/canonical-receipts view)))
        (is (= 1 (count (:receipt/corrections view))))
        (is (= original-line (fs/readFileSync
                              (path/join segments "2026-10-08T012001000Z-original.edn") "utf8")))))))

(deftest source-replay-does-not-exempt-new-invalid-uncorrected-row
  (with-real-migration true
    (fn [{:keys [base head]}]
      (is (thrown-with-msg? js/Error #"invalid receipt envelopes"
                            (cli/verify-receipts! {:base base :at head}))))))

(deftest committed-binding-removal-and-replacement-are-refused
  (doseq [operation [:remove :replace :mode :symlink]]
    (with-real-migration false
      (fn [{:keys [head config-file]}]
        (case operation
          :remove (fs/unlinkSync config-file)
          :replace (fs/writeFileSync config-file "{}\n")
          :mode (fs/chmodSync config-file 493)
          :symlink (do (fs/unlinkSync config-file) (fs/symlinkSync "other.edn" config-file)))
        (let [next (git-commit!)]
          (is (thrown? js/Error (cli/legacy-source-binding! head next)) (name operation)))))))

(deftest writer-refuses-working-config-changes-and-head-changes
  (doseq [change [:config :head]]
    (with-real-migration false
      (fn [{:keys [config-file segments]}]
        (let [target (path/join segments "2026-10-08T012004000Z-new.edn")]
          (is (thrown-with-msg? js/Error
                       (if (= change :config) #"config differs from captured HEAD" #"captured receipt HEAD changed")
                       (cli/with-append-reservation!
                        target true
                        (fn [reservation]
                          (case change
                            :config (fs/writeFileSync config-file "{}\n")
                            :head (git-commit!))
                          (cli/append-reserved-edn-line! reservation ordinary-record (pr-str ordinary-record)))))
              (name change))
          (is (not (fs/existsSync target)) "failed write removes its empty segment")
          (is (some #(str/ends-with? % ".append.lock") (fs/readdirSync segments))
              "changed state quarantines the reservation"))))))


(deftest real-imported-raw-overlap-is-rejected
  (let [dir (fs/mkdtempSync (path/join (os/tmpdir) "foresight-stream-"))
        ledger-dir (path/join dir ".ημ")
        ledger-file (path/join ledger-dir "receipts.edn")
        segments (path/join ledger-dir "receipts")
        config-dir (path/join dir "config")]
    (fs/mkdirSync segments #js {:recursive true})
    (fs/mkdirSync config-dir)
    (with-redefs [cli/root dir cli/receipt-file ledger-file]
      (try
        (cli/git-capture! ["init" "--quiet"])
        (fs/writeFileSync (path/join segments "README.md") "segments\n")
        (let [common (str (pr-str ordinary-record) "\n")
              imported (assoc ordinary-record :origin "stream-imported")
              delta (str (pr-str imported) "\n")]
          (fs/writeFileSync ledger-file common)
          (let [base (git-commit!)
                _ (fs/writeFileSync ledger-file (str common delta))
                source (git-commit!)
                source-bytes (text-bytes (str common delta))
                delta-bytes (text-bytes delta)
                descriptor {:source/base base :source/head source :source/path ".ημ/receipts.edn"
                            :source/ledger-sha256 (cli/sha256 source-bytes)
                            :source/ledger-bytes (.-length source-bytes) :source/ledger-records 2
                            :source/delta-sha256 (cli/sha256 delta-bytes)
                            :source/delta-bytes (.-length delta-bytes) :source/delta-records 1}
                declaration (assoc ordinary-record :origin "stream-declaration"
                                   :kind :receipt-stream-import :receipt/stream descriptor)
                declaration-line (str (pr-str declaration) "\n")
                anchor-text (str common declaration-line)]
            (fs/writeFileSync ledger-file anchor-text)
            (let [anchor (git-commit!)
                  config {:receipt-legacy-source/revision anchor
                          :receipt-legacy-source/sha256 (cli/sha256 (text-bytes anchor-text))}]
              (fs/writeFileSync (path/join config-dir "receipt-legacy-source.edn") (pr-str config))
              (fs/writeFileSync ledger-file common)
              (fs/writeFileSync (path/join segments "2026-10-08T012001000Z-import.edn") declaration-line)
              (let [accepted (git-commit!)]
                (is (zero? (cli/verify-receipts! {:base base :at accepted})))
                (fs/writeFileSync (path/join segments "2026-10-08T012002000Z-overlap.edn") delta)
                (let [overlap (git-commit!)]
                  (is (thrown-with-msg? js/Error #"overlaps an imported raw occurrence"
                                        (cli/verify-receipts! {:base accepted :at overlap}))))))))
        (finally (fs/rmSync dir #js {:recursive true :force true}))))))

(deftest historical-correction-and-unbound-read-replay
  (let [dir (fs/mkdtempSync (path/join (os/tmpdir) "foresight-history-"))
        ledger (path/join dir ".ημ" "receipts.edn")]
    (fs/mkdirSync (path/dirname ledger) #js {:recursive true})
    (with-redefs [cli/root dir cli/receipt-file ledger]
      (try
        (cli/git-capture! ["init" "--quiet"])
        (let [target (assoc ordinary-record :origin "history-target" :dod ["a"] :ts "yesterday")
              line (str (pr-str target) "\n")
              _ (fs/writeFileSync ledger line)
              source (git-commit!)
              correction (assoc ordinary-record :kind :correction :origin "history-correction"
                                :ts "yesterday" :correction/entries
                                [{:receipt/line 1 :receipt/origin "history-target"
                                  :receipt/source-revision source :receipt/source-sha256 (cli/sha256 (text-bytes line))
                                  :envelope/corrected-fields {:dod "a"}}])
              bytes (text-bytes (str line (pr-str correction) "\n"))
              _ (fs/writeFileSync ledger bytes)
              anchor (git-commit!)]
          (is (= "yesterday" (:ts (first (:ledger/views (cli/admit-receipt-extension! bytes bytes anchor))))))
          (is (zero? (cli/verify-receipts! {:base anchor :at anchor})))
          (let [called (atom false)]
            (with-redefs [cli/current-root-state! (fn [] {})
                          cli/require-current-clean-root! (fn [& _] {})
                          cli/read-immutable-catalog-bundle! (fn [& _] {:catalog {} :catalog-identity {}})
                          cli/validate-catalog! identity
                          law/promotion-evidence-consistent? (fn [& _] (reset! called true) false)]
              (is (false? (cli/promotion-ready-at! source #{} [] anchor anchor)))
              (is @called)))
          (is (thrown? js/Error (cli/admit-receipt-extension! (text-bytes "") bytes anchor)))
          (let [bad (assoc-in correction [:correction/entries 0 :receipt/source-sha256] (apply str (repeat 64 "0")))
                bad-bytes (text-bytes (str line (pr-str bad) "\n"))]
            (fs/writeFileSync ledger bad-bytes)
            (let [bad-anchor (git-commit!)]
              (is (thrown? js/Error (cli/verify-receipts! {:base bad-anchor :at bad-anchor}))))))
        (finally (fs/rmSync dir #js {:recursive true :force true}))))))

(deftest imported-history-requires-declaration-in-comparison-base
  (let [dir (fs/mkdtempSync (path/join (os/tmpdir) "foresight-import-history-"))
        ledger (path/join dir ".ημ" "receipts.edn")]
    (fs/mkdirSync (path/dirname ledger) #js {:recursive true})
    (with-redefs [cli/root dir cli/receipt-file ledger]
      (try
        (cli/git-capture! ["init" "--quiet"])
        (let [common (str (pr-str ordinary-record) "\n")
              _ (fs/writeFileSync ledger common)
              base (git-commit!)
              imported (assoc ordinary-record :origin "historic-import" :ts "yesterday")
              delta (str (pr-str imported) "\n")
              _ (fs/writeFileSync ledger (str common delta))
              source (git-commit!)
              full (text-bytes (str common delta))
              delta-bytes (text-bytes delta)
              descriptor {:source/base base :source/head source :source/path ".ημ/receipts.edn"
                          :source/ledger-sha256 (cli/sha256 full) :source/ledger-bytes (.-length full)
                          :source/ledger-records 2 :source/delta-sha256 (cli/sha256 delta-bytes)
                          :source/delta-bytes (.-length delta-bytes) :source/delta-records 1}
              declaration (assoc ordinary-record :origin "historical-import-declaration"
                                 :kind :receipt-stream-import :receipt/stream descriptor)
              candidate (text-bytes (str common (pr-str declaration) "\n"))
              _ (fs/writeFileSync ledger candidate)
              anchor (git-commit!)]
          (is (thrown? js/Error (cli/admit-receipt-extension! (text-bytes common) candidate anchor)))
          (is (= "yesterday" (:ts (last (:ledger/views (cli/admit-receipt-extension! candidate candidate anchor))))))
          (is (zero? (cli/verify-receipts! {:base anchor :at anchor})))
          (is (thrown? js/Error (cli/validate-working-receipt-ledger! nil anchor)))
          (let [old-declaration (assoc declaration :ts "yesterday")
                old-bytes (text-bytes (str common (pr-str old-declaration) "\n"))
                _ (fs/writeFileSync ledger old-bytes)
                old-anchor (git-commit!)
                replay (cli/admit-receipt-extension! old-bytes old-bytes old-anchor)]
            (is (= "yesterday" (:ts (second (:ledger/views replay)))))
            (is (= "yesterday" (:ts (last (:ledger/views replay)))))
            (is (thrown? js/Error (cli/admit-receipt-extension! (text-bytes common) old-bytes old-anchor))))
          ;; Selecting a newer source in a binding does not make its import
          ;; historical relative to the earlier comparison base.
          (let [config-dir (path/join dir "config")
                segments (path/join dir ".ημ" "receipts")]
            (fs/mkdirSync config-dir)
            (fs/mkdirSync segments)
            (fs/writeFileSync (path/join config-dir "receipt-legacy-source.edn")
                              (pr-str {:receipt-legacy-source/revision anchor
                                       :receipt-legacy-source/sha256 (cli/sha256 candidate)}))
            (fs/writeFileSync ledger common)
            (fs/writeFileSync (path/join segments "2026-10-08T012001000Z-import.edn")
                              (str (pr-str declaration) "\n"))
            (let [bound (git-commit!)]
              (is (thrown? js/Error (cli/verify-receipts! {:base base :at bound})))))
          (let [invalid-new (assoc ordinary-record :origin "new" :ts "yesterday")
                extended (text-bytes (str (.toString candidate "utf8") (pr-str invalid-new) "\n"))]
            (is (thrown? js/Error (cli/admit-receipt-extension! candidate extended anchor)))))
        (finally (fs/rmSync dir #js {:recursive true :force true}))))))

(deftest new-import-corrections-can-interpret-trusted-common-timestamp
  (let [dir (fs/mkdtempSync (path/join (os/tmpdir) "foresight-common-history-"))
        ledger (path/join dir ".ημ" "receipts.edn")]
    (fs/mkdirSync (path/dirname ledger) #js {:recursive true})
    (with-redefs [cli/root dir cli/receipt-file ledger]
      (try
        (cli/git-capture! ["init" "--quiet"])
        (let [target (assoc ordinary-record :origin "old-common" :ts "yesterday" :dod ["a"])
              common (str (pr-str target) "\n")
              _ (fs/writeFileSync ledger common)
              base (git-commit!)
              entry {:receipt/line 1 :receipt/origin "old-common" :receipt/source-revision base
                     :receipt/source-sha256 (cli/sha256 (text-bytes common))
                     :envelope/corrected-fields {:dod "a"}}
              source-correction (assoc ordinary-record :kind :correction :origin "source-correction"
                                       :correction/entries [entry])
              source-delta (str (pr-str source-correction) "\n")
              source-bytes (text-bytes (str common source-delta))
              _ (fs/writeFileSync ledger source-bytes)
              source (git-commit!)
              descriptor {:source/base base :source/head source :source/path ".ημ/receipts.edn"
                          :source/ledger-sha256 (cli/sha256 source-bytes) :source/ledger-bytes (.-length source-bytes)
                          :source/ledger-records 2 :source/delta-sha256 (cli/sha256 (text-bytes source-delta))
                          :source/delta-bytes (.-length (text-bytes source-delta)) :source/delta-records 1}
              declaration (assoc ordinary-record :origin "new-declaration" :kind :receipt-stream-import :receipt/stream descriptor)
              canonical-correction (assoc source-correction :origin "canonical-correction")
              head-bytes (text-bytes (str common (pr-str canonical-correction) "\n" (pr-str declaration) "\n"))
              _ (fs/writeFileSync ledger head-bytes)
              anchor (git-commit!)
              admission (cli/admit-receipt-extension! (text-bytes common) head-bytes anchor)]
          (is (= "yesterday" (:ts (first (:ledger/views admission)))))
          (is (= "a" (:dod (first (:ledger/views admission)))))
          (is (= 1 (:ledger/imported-receipts admission))))
        (finally (fs/rmSync dir #js {:recursive true :force true}))))))

(defmethod test/report [::test/default :end-run-tests] [summary]
  (set! (.-exitCode js/process) (if (test/successful? summary) 0 1)))

(test/run-tests 'evidence-integration-test)
