;; SPDX-License-Identifier: LGPL-3.0-or-later
(ns evidence-test
  (:require [cljs.test :as test :refer [deftest is]]
            [foresight.evidence :as evidence]))

(def valid-catalog
  {:catalog/version 1
   :catalog/repositories
   {"repo"
    {:repository/path "repo"
     :repository/gates
     [{:gate/id :repo/unit
       :gate/kind :unit
       :gate/execution :local
       :gate/command ["tool" "test"]
       :gate/source "repo/README.md"}
      {:gate/id :repo/integration
       :gate/kind :integration
       :gate/execution :local
       :gate/command ["tool" "integration"]
       :gate/source "repo/README.md"}
      {:gate/id :repo/live
       :gate/kind :live-smoke
       :gate/execution :external
       :gate/source "repo/.github/workflows/deploy.yml"
       :gate/reason "Requires the target host"}]}}})

(def test-catalog-identity
  {:catalog/path "config/quality-gates.edn"
   :catalog/sha256 (apply str (repeat 64 "a"))})

(def revision-a (apply str (repeat 40 "1")))

(def revision-b (apply str (repeat 40 "2")))

(defn recorded-result [gate-id outcome revision]
  (let [gate (or (some #(when (= gate-id (:gate/id %)) %)
                       (get-in valid-catalog
                               [:catalog/repositories "repo"
                                :repository/gates]))
                 {:gate/execution :local
                  :gate/command ["tool" "test"]
                  :gate/source "repo/README.md"})]
    (cond-> {:gate/id gate-id
             :result/outcome outcome
             :result/execution (:gate/execution gate)
             :result/catalog test-catalog-identity
             :result/source {:source/path (:gate/source gate)
                             :source/repository "repo"
                             :source/revision revision}
             :result/revision revision}
      (:gate/command gate)
      (assoc :result/command (:gate/command gate))

      (and (= :local (:gate/execution gate))
           (= :passed outcome))
      (assoc :result/exit 0))))

(defn receipt-for [result]
  (let [source (:result/source result)]
    {:ts "2026-08-29T17:22:40Z"
     :kind :test-run
     :repo "."
     :origin evidence/evidence-receipt-origin
     :evidence/schema 2
     :evidence/adapter "nbb/node@test"
     :owner "foresight-evidence-runner"
     :dod "Retain one exact gate result for immutable promotion review"
     :pi "eta-mu"
     :host "test"
     :manifest [(:catalog/path (:result/catalog result))
                (:source/path source)]
     :refs [(str (:source/repository source) "@" (:result/revision result))
            (str (:gate/id result))]
     :evidence/result result}))

(defn immutable-ledger [results]
  {:ledger/identity
   {:ledger/path evidence/receipt-ledger-path
    :ledger/revision (apply str (repeat 40 "c"))
    :ledger/sha256 (apply str (repeat 64 "d"))}
   :ledger/records (mapv receipt-for results)})

(deftest canonical-receipt-envelope-matches-the-v2-contract
  (let [receipt (dissoc (receipt-for
                         (recorded-result :repo/unit :passed revision-a))
                        :repo)]
    (is (evidence/receipt-envelope? receipt))
    (is (false? (evidence/receipt-envelope? (dissoc receipt :origin))))
    (is (false? (evidence/receipt-envelope? 42)))))

(def valid-receipt-stream-descriptor
  {:source/base revision-a
   :source/head revision-b
   :source/path ".ημ/receipts.edn"
   :source/ledger-sha256 (apply str (repeat 64 "a"))
   :source/ledger-bytes 100
   :source/ledger-records 5
   :source/delta-sha256 (apply str (repeat 64 "b"))
   :source/delta-bytes 60
   :source/delta-records 2})

(def valid-receipt-stream-import
  (-> (receipt-for (recorded-result :repo/unit :passed revision-a))
      (dissoc :repo :evidence/schema :evidence/adapter :evidence/result)
      (assoc :kind :receipt-stream-import
             :origin "receipt-stream-shape-fixture"
             :receipt/stream valid-receipt-stream-descriptor)))

(deftest receipt-stream-descriptor-admits-declared-shape-without-source-proof
  (is (true? (evidence/receipt-stream-descriptor? valid-receipt-stream-descriptor)))
  (let [counts [:source/ledger-bytes :source/ledger-records
                :source/delta-bytes :source/delta-records]]
    (is (true? (evidence/receipt-stream-descriptor?
                (reduce #(assoc %1 %2 1) valid-receipt-stream-descriptor counts)))
        "positive minimum counts and equal ledger/delta counts permit an empty source base")
    (is (true? (evidence/receipt-stream-descriptor?
                (assoc valid-receipt-stream-descriptor
                       :source/delta-bytes 101 :source/delta-records 6)))
        "shape does not authenticate claimed counts or impose an undeclared ordering"))
  (is (true? (evidence/receipt-stream-descriptor?
              (assoc valid-receipt-stream-descriptor :source/head revision-a)))
      "full commit strings are shaped here; source existence and ancestry belong to the adapter"))

(deftest receipt-stream-descriptor-is-a-closed-nine-key-map
  (doseq [field (keys valid-receipt-stream-descriptor)]
    (is (false? (evidence/receipt-stream-descriptor?
                  (dissoc valid-receipt-stream-descriptor field)))
        (str "missing descriptor field " field)))
  (doseq [field [:source/repository :source/ordinal :receipt/stream "source/base"]]
    (is (false? (evidence/receipt-stream-descriptor?
                  (assoc valid-receipt-stream-descriptor field "extra")))
        (str "unsupported descriptor field " (pr-str field))))
  (is (false? (evidence/receipt-stream-descriptor?
                (-> valid-receipt-stream-descriptor
                    (dissoc :source/base)
                    (assoc "source/base" revision-a))))
      "a string key cannot replace its declared keyword even with nine total keys"))

(deftest receipt-stream-descriptor-refuses-nonmap-values
  (doseq [value [nil false 42 "descriptor" :descriptor []
                (seq valid-receipt-stream-descriptor)]]
    (is (false? (evidence/receipt-stream-descriptor? value))
        (str "descriptor type " (pr-str value)))))

(deftest receipt-stream-descriptor-requires-full-forty-lowercase-commit-strings
  (doseq [field [:source/base :source/head]
          value [nil 42 :revision [] "" " "
                 (apply str (repeat 39 "a"))
                 (apply str (repeat 41 "a"))
                 (apply str (repeat 64 "a"))
                 (apply str (repeat 40 "A"))
                 (apply str (repeat 40 "g"))
                 (str " " revision-a) (str revision-a "\n")]]
    (is (false? (evidence/receipt-stream-descriptor?
                  (assoc valid-receipt-stream-descriptor field value)))
        (str "commit field " field " value " (pr-str value)))))

(deftest receipt-stream-shape-keeps-existing-git-id-compatibility
  (is (true? (evidence/git-commit-id? revision-a)))
  (is (true? (evidence/git-commit-id? (apply str (repeat 64 "a")))))
  (is (true? (evidence/receipt-stream-descriptor?
              (assoc valid-receipt-stream-descriptor
                     :source/base (apply str (repeat 40 "0"))
                     :source/ledger-sha256 (apply str (repeat 64 "0")))))
      "well-shaped identities do not claim that a source or digest was verified"))

(deftest receipt-stream-descriptor-requires-exact-lowercase-sha256-strings
  (doseq [field [:source/ledger-sha256 :source/delta-sha256]
          value [nil 42 :digest [] "" " "
                 (apply str (repeat 40 "a"))
                 (apply str (repeat 63 "a"))
                 (apply str (repeat 65 "a"))
                 (apply str (repeat 64 "A"))
                 (apply str (repeat 64 "g"))
                 (str (apply str (repeat 64 "a")) "\n")]]
    (is (false? (evidence/receipt-stream-descriptor?
                  (assoc valid-receipt-stream-descriptor field value)))
        (str "digest field " field " value " (pr-str value)))))

(deftest receipt-stream-descriptor-requires-the-literal-ledger-path
  (doseq [value [nil 42 :receipts "" " " "./.ημ/receipts.edn"
                ".eta-mu/receipts.edn" ".ημ/receipts.edn/" ".ημ/receipts.edn\n"
                "/repo/.ημ/receipts.edn"]]
    (is (false? (evidence/receipt-stream-descriptor?
                  (assoc valid-receipt-stream-descriptor :source/path value)))
        (str "source path " (pr-str value)))))

(deftest receipt-stream-descriptor-requires-positive-integer-counts
  (doseq [field [:source/ledger-bytes :source/ledger-records
                :source/delta-bytes :source/delta-records]
          value [nil false "1" :one 0 -1 1.5 [] {}]]
    (is (false? (evidence/receipt-stream-descriptor?
                  (assoc valid-receipt-stream-descriptor field value)))
        (str "count field " field " value " (pr-str value)))))

(deftest receipt-stream-trigger-recognizes-kind-or-descriptor-presence
  (doseq [receipt [valid-receipt-stream-import
                  {:kind :receipt-stream-import}
                  {:kind :receipt-stream-import :receipt/stream nil}
                  {:receipt/stream valid-receipt-stream-descriptor}
                  {:receipt/stream nil}
                  {:kind :observation :receipt/stream false}
                  {:kind :correction :receipt/stream []}]]
    (is (true? (evidence/receipt-stream-trigger? receipt))
        (str "import grammar must inspect " (pr-str receipt)))))

(deftest receipt-stream-trigger-leaves-ordinary-and-nonmap-values-alone
  (doseq [receipt [nil false 42 :receipt-stream-import []
                  {} {:kind :observation} {:kind :correction}
                  {:kind "receipt-stream-import"}
                  {:kind :observation "receipt/stream" valid-receipt-stream-descriptor}]]
    (is (false? (evidence/receipt-stream-trigger? receipt))
        (str "no keyword import trigger in " (pr-str receipt)))))

(deftest receipt-stream-import-requires-its-kind-and-valid-descriptor
  (is (true? (evidence/receipt-stream-import? valid-receipt-stream-import)))
  (doseq [kind [nil :observation :correction "receipt-stream-import"]]
    (let [receipt (assoc valid-receipt-stream-import :kind kind)]
      (is (true? (evidence/receipt-stream-trigger? receipt)))
      (is (false? (evidence/receipt-stream-import? receipt))
          (str "descriptor presence does not authorize kind " (pr-str kind)))))
  (doseq [receipt [(dissoc valid-receipt-stream-import :receipt/stream)
                  (assoc valid-receipt-stream-import :receipt/stream nil)
                  (assoc valid-receipt-stream-import :receipt/stream {})
                  (update valid-receipt-stream-import :receipt/stream dissoc :source/head)
                  (assoc-in valid-receipt-stream-import [:receipt/stream :source/delta-bytes] 0)
                  (assoc-in valid-receipt-stream-import [:receipt/stream :source/head]
                            (apply str (repeat 64 "a")))]]
    (is (true? (evidence/receipt-stream-trigger? receipt)))
    (is (false? (evidence/receipt-stream-import? receipt))
        "an import trigger with missing or malformed descriptor is refused")))

(deftest receipt-stream-import-preserves-strict-native-envelope-requirements
  (doseq [field [:ts :origin :owner :dod :pi :host]]
    (is (false? (evidence/receipt-stream-import?
                  (dissoc valid-receipt-stream-import field)))
        (str "missing native envelope field " field))
    (doseq [value [nil "" " " :text]]
      (is (false? (evidence/receipt-stream-import?
                    (assoc valid-receipt-stream-import field value)))
          (str "invalid native envelope field " field " value " (pr-str value)))))
  (doseq [field [:manifest :refs]
          value [nil "entry" '("entry") [""] [42]]]
    (is (false? (evidence/receipt-stream-import?
                  (assoc valid-receipt-stream-import field value)))
        (str "native envelope vector field " field " value " (pr-str value))))
  (doseq [receipt [nil false 42 []]]
    (is (false? (evidence/receipt-stream-import? receipt))
        (str "import record type " (pr-str receipt))))
  (is (true? (evidence/receipt-stream-import?
              (assoc valid-receipt-stream-import :manifest [] :refs [])))
      "the existing native envelope permits empty string vectors"))

(deftest validates-gate-catalogs
  (is (evidence/valid-catalog? valid-catalog))
  (is (= :gate/command
         (:error
          (first
           (evidence/catalog-errors
            (assoc-in valid-catalog
                      [:catalog/repositories "repo" :repository/gates 0 :gate/command]
                      ["tool" ""]))))))
  (is (= :gate/reason
         (:error
          (first
           (evidence/catalog-errors
            (update-in valid-catalog
                       [:catalog/repositories "repo" :repository/gates 2]
                       dissoc :gate/reason)))))))

(deftest rejects-duplicate-gate-identities
  (let [duplicate (assoc-in valid-catalog
                            [:catalog/repositories "other"]
                            {:repository/path "other"
                             :repository/gates
                             [(get-in valid-catalog
                                      [:catalog/repositories "repo"
                                       :repository/gates 0])]})]
    (is (= {:error :gate/id-duplicates :gate/ids [:repo/unit]}
           (last (evidence/catalog-errors duplicate))))))

(deftest malformed-gate-collections-return-structured-errors
  (let [malformed (assoc-in valid-catalog
                            [:catalog/repositories "repo" :repository/gates]
                            42)]
    (is (= :repository/gates
           (:error (first (evidence/catalog-errors malformed)))))
    (is (false? (evidence/valid-catalog? malformed)))
    (is (false? (evidence/promotion-evidence-consistent?
                 malformed test-catalog-identity revision-a
                 #{:repo/unit}
                 [(recorded-result :repo/unit :passed revision-a)]
                 (immutable-ledger
                  [(recorded-result :repo/unit :passed revision-a)]))))))

(deftest malformed-mixed-identities-remain-total
  (let [unit (get-in valid-catalog
                     [:catalog/repositories "repo" :repository/gates 0])
        malformed (assoc-in valid-catalog
                            [:catalog/repositories "repo" :repository/gates]
                            [unit unit
                             (assoc unit :gate/id 42)
                             (assoc unit :gate/id 42)])]
    (is (= {:error :gate/id-duplicates :gate/ids [:repo/unit]}
           (last (evidence/catalog-errors malformed))))
    (is (false? (evidence/valid-catalog? malformed))))
  (let [errors (evidence/catalog-inventory-errors
                {:catalog/repositories {"repo" {} 42 {}}}
                #{})]
    (is (= 2 (count errors)))
    (is (= #{"repo" 42} (set (map :repository errors))))))

(deftest catalog-repositories-must-be-actionable-direct-submodules
  (is (empty? (evidence/catalog-inventory-errors valid-catalog #{"repo"})))
  (is (= [{:error :catalog/repository-not-actionable-submodule
           :repository "repo"}]
         (evidence/catalog-inventory-errors valid-catalog #{"other"}))))

(deftest selects-repositories-and-kinds
  (is (= [:repo/unit]
         (mapv :gate/id
               (evidence/select-gates valid-catalog ["repo"] #{:unit}))))
  (is (= [] (evidence/select-gates valid-catalog ["missing"] #{:unit}))))

(deftest unavailable-and-blocked-never-pass
  (doseq [outcome [:failed :blocked :unavailable]]
    (is (false? (evidence/satisfied? {:result/outcome outcome}))))
  (is (false? (evidence/satisfied? {:result/outcome :not-applicable
                                    :result/reason "No browser surface"})))
  (is (evidence/satisfied? {:result/outcome :not-applicable
                            :result/reason "No browser surface"
                            :result/approved-by "review/42"}))
  (is (false? (evidence/promotion-satisfied?
               {:result/outcome :not-applicable
                :result/reason "No browser surface"
                :result/approved-by "review/42"})))
  (is (evidence/promotion-satisfied? {:result/outcome :passed})))

(deftest validates-result-outcomes-and-not-applicable-approval
  (let [passed (recorded-result :repo/unit :passed revision-a)]
    (is (evidence/valid-result? passed))
    (is (= :result/command
           (:error (first (evidence/result-errors
                           (dissoc passed :result/command))))))
    (is (= :result/catalog
           (:error (first (evidence/result-errors
                           (assoc-in passed
                                     [:result/catalog :catalog/sha256]
                                     "not-a-digest"))))))
    (is (= :result/source-revision
           (:error (first (evidence/result-errors
                           (assoc-in passed
                                     [:result/source :source/revision]
                                     revision-b))))))
    (is (= :result/local-passed-exit
           (:error (first (filter #(= :result/local-passed-exit (:error %))
                                  (evidence/result-errors
                                   (assoc passed :result/exit 7)))))))
    (is (= :result/local-nonpass-exit
           (:error (first (filter #(= :result/local-nonpass-exit (:error %))
                                  (evidence/result-errors
                                   (assoc passed
                                          :result/outcome :failed))))))))
  (is (= :result/outcome
         (:error (first (evidence/result-errors
                         (recorded-result :repo/unit :greenish
                                          revision-a))))))
  (is (= :result/not-applicable-approval
         (:error (first (evidence/result-errors
                         (assoc (recorded-result :repo/e2e
                                                :not-applicable
                                                revision-a)
                                :result/reason "No user surface"))))))
  (doseq [symbolic-revision ["main" "v1.0.0" "abc123"
                             (apply str (repeat 40 "A"))]]
    (let [symbolic (recorded-result :repo/unit :passed symbolic-revision)
          errors (set (map :error (evidence/result-errors symbolic)))]
      (is (false? (evidence/valid-result? symbolic)))
      (is (contains? errors :result/source))
      (is (contains? errors :result/passed-revision))))
  (is (evidence/valid-result?
       (recorded-result :repo/unit :passed (apply str (repeat 64 "3"))))))

(deftest summary-keeps-the-strongest-non-pass-visible
  (is (= {:result/outcome :blocked
          :result/counts {:blocked 1 :passed 1 :unavailable 1}
          :result/satisfied? false}
         (evidence/summarize-results
          [(recorded-result :repo/unit :passed revision-a)
           (recorded-result :repo/e2e :unavailable revision-a)
           (recorded-result :repo/live :blocked revision-a)])))
  (is (= {:result/outcome :unavailable
          :result/counts {}
          :result/satisfied? false}
         (evidence/summarize-results [])))
  (is (= {:result/outcome :failed
          :result/counts {}
          :result/satisfied? false
          :result/errors [{:error :results/type :results 42}]}
         (evidence/summarize-results 42))))

(deftest invalid-result-data-fails-the-summary
  (let [summary (evidence/summarize-results
                 [(recorded-result :repo/unit :unknown revision-a)])]
    (is (= :failed (:result/outcome summary)))
    (is (false? (:result/satisfied? summary)))
    (is (= :result/outcome (get-in summary [:result/errors 0 :error])))))

(deftest malformed-result-values-return-structured-errors
  (is (= [{:error :result/type :result 42}]
         (evidence/result-errors 42)))
  (is (false? (evidence/valid-result? 42)))
  (let [summary (evidence/summarize-results [42])]
    (is (= :failed (:result/outcome summary)))
    (is (= {} (:result/counts summary)))
    (is (= :result/type (get-in summary [:result/errors 0 :error])))))

(deftest promotion-is-closed-over-required-gates
  (let [revision revision-a
        passed [(recorded-result :repo/unit :passed revision)
                (recorded-result :repo/integration :passed revision)]]
    (is (evidence/promotion-evidence-consistent?
         valid-catalog test-catalog-identity revision
         #{:repo/unit :repo/integration} passed
         (immutable-ledger passed)))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision
                 #{:repo/unit :repo/e2e} passed
                 (immutable-ledger passed))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision
                 #{:repo/unit}
                 [(recorded-result :repo/unit :unavailable revision)]
                 (immutable-ledger
                  [(recorded-result :repo/unit :unavailable revision)]))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision #{} []
                 (immutable-ledger []))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision 42 []
                 (immutable-ledger []))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision #{} 42
                 (immutable-ledger []))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision
                 #{:repo/unit} [(first passed)] nil)))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity "not-a-git-commit"
                 #{:repo/unit} [(first passed)]
                 (immutable-ledger [(first passed)]))))))

(deftest promotion-requires-one-unambiguous-target-revision
  (let [unit (recorded-result :repo/unit :passed revision-a)
        integration (recorded-result :repo/integration :passed revision-b)]
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision-a
                 #{:repo/unit :repo/integration}
                 [unit integration]
                 (immutable-ledger [unit integration]))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity
                 "" #{:repo/unit} [unit]
                 (immutable-ledger [unit]))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity
                 revision-a #{:repo/unit} [unit unit]
                 (immutable-ledger [unit unit]))))))

(deftest coverage-cannot-promote-without-immutable-report-evidence
  (let [coverage-gate (assoc (get-in valid-catalog
                                     [:catalog/repositories "repo"
                                      :repository/gates 0])
                             :gate/id :repo/coverage
                             :gate/kind :coverage)
        catalog (assoc-in valid-catalog
                          [:catalog/repositories "repo" :repository/gates]
                          [coverage-gate])
        result (recorded-result :repo/coverage :passed revision-a)]
    (is (evidence/valid-result? result))
    (is (false? (evidence/automatic-promotion-supported? coverage-gate)))
    (is (false? (evidence/promotion-evidence-consistent?
                 catalog test-catalog-identity revision-a
                 #{:repo/coverage} [result]
                 (immutable-ledger [result]))))))

(deftest promotion-results-must-match-the-trusted-catalog-snapshot
  (let [revision revision-a
        result (recorded-result :repo/unit :passed revision)
        consistent? #(evidence/promotion-evidence-consistent?
                      valid-catalog test-catalog-identity revision
                      #{:repo/unit} [%]
                      (immutable-ledger [result]))]
    (is (consistent? result))
    (is (false? (consistent? (assoc result :result/command ["true"]))))
    (is (false? (consistent? (assoc result :result/exit 7))))
    (is (false? (consistent? (assoc result :result/execution :external))))
    (is (false? (consistent? (assoc result
                                    :result/catalog
                                    (assoc test-catalog-identity
                                           :catalog/sha256
                                           (apply str (repeat 64 "b")))))))
    (is (false? (consistent? (assoc-in result
                                       [:result/source :source/path]
                                       "repo/forged.edn"))))
    (is (false? (consistent? (assoc-in result
                                       [:result/source :source/repository]
                                       "other"))))
    (let [failed (assoc (recorded-result :repo/unit :failed revision)
                        :result/exit 7)
          edited-pass (assoc failed :result/outcome :passed :result/exit 0)]
      (is (evidence/valid-result? edited-pass))
      (is (false? (evidence/promotion-evidence-consistent?
                   valid-catalog test-catalog-identity revision
                   #{:repo/unit} [edited-pass]
                   (immutable-ledger [failed])))))))

(deftest promotion-requires-an-exact-immutable-receipt
  (let [result (recorded-result :repo/unit :passed revision-a)
        ledger (immutable-ledger [result])
        receipt (first (:ledger/records ledger))
        legacy-receipt (dissoc receipt :evidence/schema :evidence/adapter)
        not-applicable (-> result
                           (assoc :result/outcome :not-applicable
                                  :result/reason "No user surface"
                                  :result/approved-by "review/42")
                           (dissoc :result/exit))]
    (is (evidence/evidence-receipt? (first (:ledger/records ledger))))
    (is (evidence/evidence-receipt? legacy-receipt))
    (is (false? (evidence/receipt-attests-result? legacy-receipt result)))
    (is (false? (evidence/evidence-receipt?
                 (assoc receipt :evidence/adapter ""))))
    (is (evidence/immutable-receipt-ledger? ledger))
    (is (evidence/promotion-evidence-consistent?
         valid-catalog test-catalog-identity revision-a
         #{:repo/unit} [result]
         (update ledger :ledger/records
                 #(conj % (first %)))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision-a
                 #{:repo/unit} [result]
                 (assoc-in ledger [:ledger/identity :ledger/sha256] "forged"))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision-a
                 #{:repo/unit} [result]
                 (update ledger :ledger/records empty))))
    (is (false? (evidence/promotion-evidence-consistent?
                 valid-catalog test-catalog-identity revision-a
                 #{:repo/unit} [not-applicable]
                 (immutable-ledger [not-applicable]))))))

;; BEGIN receipt-correction-view RED contract
;; The only new production API is receipt-correction-view [items verified-targets].
;; The RED commit used a test-only absent-API resolver. GREEN calls the now
;; implemented API directly, retaining every assertion. Git, ancestry, UTF-8 and
;; including-LF digest verification belong to the adapter. Digests below are
;; opaque fixture identities, not hashes independently verified by this suite.
;;
;; Error maps use :error with the precise keywords asserted below; additional
;; ordinary-envelope errors may coexist with a correction refusal. Refused
;; entries must not apply even a subset of their fields. Duplicate targets are
;; refused globally, including across correction records/source identities.
;; Successful provenance is the original documentary entry plus
;; :correction/line (the absolute line of the correction record).
(defn correction-view-under-test [items verified-targets]
  (evidence/receipt-correction-view items verified-targets))

(def correction-source-revision
  "91a6fc6b27264da4b8c9b8cd7919f153adea0ff2")
(def correction-source-digest (apply str (repeat 64 "a")))

(def correction-original
  (assoc (receipt-for (recorded-result :repo/unit :passed revision-a))
         :manifest ".ημ/receipts.edn,automation:follow-up"
         :refs "root1069,request6044932186"
         :dod [" first objective " "second; objective"]
         :pi :cephalon/character-memory))

(def correction-fields
  {:manifest [".ημ/receipts.edn,automation:follow-up"]
   :refs ["root1069,request6044932186"]
   :dod " first objective ; second; objective"
   :pi "cephalon/character-memory"})

(defn documentary-correction [entries]
  {:ts "2026-10-07T22:09:45.368Z"
   :kind :correction
   :repo "open-hax/foresight"
   :origin "documentary-envelope-correction"
   :owner "test-coordinator"
   :dod "Retain bound envelope interpretation without rewriting history"
   :pi "cephalon/character-memory"
   :host "test"
   :manifest [".ημ/receipts.edn"]
   :refs ["append correction or leave originals; containing repo supplies attribution"]
   :correction/entries entries})

(defn correction-fixture
  ([] (correction-fixture correction-original correction-fields))
  ([original fields]
   (let [entry {:receipt/line 267
                :receipt/origin (:origin original)
                :receipt/source-revision correction-source-revision
                :receipt/source-sha256 correction-source-digest
                :envelope/corrected-fields fields}
         binding (assoc (dissoc entry :envelope/corrected-fields)
                        :binding/verified? true :receipt/record original)]
     {:items [{:receipt/line 267 :receipt/record original}
              {:receipt/line 278 :receipt/record (documentary-correction [entry])}]
      :targets {[correction-source-revision 267 correction-source-digest] binding}
      :entry entry})))

(def correction-binding-key
  [correction-source-revision 267 correction-source-digest])

(defn fixture-entry [fixture entry]
  (assoc-in fixture [:items 1 :receipt/record :correction/entries] [entry]))

(defn expect-correction-success! [scenario {:keys [items targets]} views provenance]
  (is (= {:receipt/originals (mapv :receipt/record items)
          :receipt/views views
          :receipt/corrections provenance
          :receipt/errors []}
         (correction-view-under-test items targets))
      scenario))

(defn expect-correction-refusal! [scenario expected-code {:keys [items targets]}]
  (let [result (correction-view-under-test items targets)]
    (is (contains? (set (map :error (:receipt/errors result))) expected-code)
        (str scenario " must report " expected-code))
    (is (= {:receipt/views (mapv :receipt/record items)
            :receipt/corrections []}
           (select-keys result [:receipt/views :receipt/corrections]))
        (str scenario " must retain every original view and admit no correction"))))

(deftest receipt-correction-valid-ordinary-records-remain-exact
  (let [ordinary (dissoc (receipt-for (recorded-result :repo/unit :passed revision-a))
                         :repo)
        ;; Kind alone does not activate documentary grammar.
        kind-only (assoc ordinary :kind :correction)
        items [{:receipt/line 17 :receipt/record ordinary}
               {:receipt/line 259 :receipt/record kind-only}]]
    (expect-correction-success! "valid ordinary maps need no correction or repo field"
                                {:items items :targets {}}
                                [ordinary kind-only] [])))

(deftest receipt-correction-empty-input-has-empty-result
  (expect-correction-success! "empty ledger selection"
                              {:items [] :targets {}} [] []))

(deftest receipt-correction-documentary-syntax-keeps-originals-and-evidence
  (let [{:keys [items entry] :as fixture} (correction-fixture)
        corrected (assoc correction-original
                         :manifest [".ημ/receipts.edn,automation:follow-up"]
                         :refs ["root1069,request6044932186"]
                         :dod " first objective ; second; objective"
                         :pi "cephalon/character-memory")]
    (expect-correction-success!
     "four exact interpretations preserve ts/owner/origin/kind and evidence/result"
     fixture [corrected (:receipt/record (second items))]
     [(assoc entry :correction/line 278)])))

(deftest receipt-correction-converts-each-permitted-invalid-field-independently
  (let [ordinary (receipt-for (recorded-result :repo/unit :passed revision-a))]
    (doseq [[field original-value replacement]
            [[:manifest "path-a,path-b" ["path-a,path-b"]]
             [:refs "ref-a,ref-b" ["ref-a,ref-b"]]
             [:dod ["alpha " " beta"] "alpha ;  beta"]
             [:pi :cephalon/character-memory "cephalon/character-memory"]
             [:pi :eta-mu "eta-mu"]]]
      (let [original (assoc ordinary field original-value)
            {:keys [items entry] :as fixture}
            (correction-fixture original {field replacement})]
        (expect-correction-success!
         (str "exact conversion of " field " from " (pr-str original-value))
         fixture [(assoc original field replacement)
                  (:receipt/record (second items))]
         [(assoc entry :correction/line 278)])))))

(deftest receipt-correction-supports-full-sha256-source-identity
  (let [{:keys [entry targets] :as fixture} (correction-fixture)
        revision (apply str (repeat 64 "b"))
        entry (assoc entry :receipt/source-revision revision)
        binding (assoc (get targets correction-binding-key)
                       :receipt/source-revision revision)
        fixture (assoc (fixture-entry fixture entry)
                       :targets {[revision 267 correction-source-digest] binding})]
    (expect-correction-success!
     "full SHA256 commit identity is matched, not verified by the pure law"
     fixture [(merge correction-original correction-fields)
              (:receipt/record (second (:items fixture)))]
     [(assoc entry :correction/line 278)])))

(deftest receipt-correction-semantic-evidence-remains-for-existing-outer-validator
  (let [original (assoc-in correction-original [:evidence/result :result/exit] 7)
        {:keys [items entry] :as fixture} (correction-fixture original correction-fields)]
    (expect-correction-success!
     "envelope interpretation retains a semantically invalid result unchanged"
     fixture [(merge original correction-fields) (:receipt/record (second items))]
     [(assoc entry :correction/line 278)])))

(deftest receipt-correction-key-presence-requires-correction-kind
  (doseq [entries [nil []]]
    (expect-correction-refusal!
     (str "entries key present on non-correction kind, value " (pr-str entries))
     :receipt-correction/kind
     {:items [{:receipt/line 278
               :receipt/record (assoc (documentary-correction entries)
                                      :kind :observation)}]
      :targets {}})))

(deftest receipt-correction-rejects-empty-or-nonvector-entries
  (let [fixture (correction-fixture)]
    (doseq [entries [nil [] {} (list (:entry fixture)) "entries"]]
      (expect-correction-refusal!
       (str "entries must be a nonempty vector: " (pr-str entries))
       :receipt-correction/entries
       (assoc-in fixture [:items 1 :receipt/record :correction/entries] entries)))))

(deftest receipt-correction-rejects-nonmap-entry
  (expect-correction-refusal! "entry is not a map" :receipt-correction/entry-type
                              (fixture-entry (correction-fixture) 42)))

(deftest receipt-correction-requires-exact-documentary-entry-fields
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [field (keys entry)]
      (expect-correction-refusal! (str "missing entry key " field)
                                  :receipt-correction/entry-fields
                                  (fixture-entry fixture (dissoc entry field))))
    (expect-correction-refusal! "extra entry key is not documentary grammar"
                                :receipt-correction/entry-fields
                                (fixture-entry fixture (assoc entry :extra true)))))

(deftest receipt-correction-requires-positive-absolute-target-line
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [line [0 -1 267.5 "267" nil]]
      (expect-correction-refusal! (str "invalid target ordinal " (pr-str line))
                                  :receipt-correction/target-line
                                  (fixture-entry fixture (assoc entry :receipt/line line))))))

(deftest receipt-correction-requires-nonblank-entry-origin
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [origin [nil "" "  " :origin]]
      (expect-correction-refusal! (str "invalid entry origin " (pr-str origin))
                                  :receipt-correction/entry-origin
                                  (fixture-entry fixture (assoc entry :receipt/origin origin))))))

(deftest receipt-correction-requires-full-lowercase-source-identities
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [revision ["main" "91a6fc6" (apply str (repeat 40 "A")) nil]]
      (expect-correction-refusal! (str "invalid source revision " (pr-str revision))
                                  :receipt-correction/source-revision
                                  (fixture-entry fixture
                                                 (assoc entry :receipt/source-revision revision))))
    (doseq [digest ["a" (apply str (repeat 64 "A")) nil]]
      (expect-correction-refusal! (str "invalid source SHA256 " (pr-str digest))
                                  :receipt-correction/source-sha256
                                  (fixture-entry fixture
                                                 (assoc entry :receipt/source-sha256 digest))))))

(deftest receipt-correction-rejects-missing-binding-and-wrong-lookup-identity
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (expect-correction-refusal! "no verified fact for the exact lookup triple"
                                :receipt-correction/binding-missing
                                (assoc fixture :targets {}))
    (doseq [[field value] [[:receipt/source-revision revision-b]
                          [:receipt/source-sha256 (apply str (repeat 64 "b"))]]]
      (expect-correction-refusal! (str "lookup uses a different " field)
                                  :receipt-correction/binding-missing
                                  (fixture-entry fixture (assoc entry field value))))))

(deftest receipt-correction-rejects-malformed-binding-container-or-value
  (let [fixture (correction-fixture)]
    (doseq [targets [nil [] "facts"]]
      (expect-correction-refusal! (str "facts table is not a map: " (pr-str targets))
                                  :receipt-correction/bindings-type
                                  (assoc fixture :targets targets)))
    (expect-correction-refusal! "bound fact is not a map"
                                :receipt-correction/binding-type
                                (assoc fixture :targets {correction-binding-key 42}))))

(deftest receipt-correction-requires-complete-binding-facts
  (let [{:keys [targets] :as fixture} (correction-fixture)]
    (doseq [field [:receipt/line :receipt/origin :receipt/source-revision
                  :receipt/source-sha256 :receipt/record]]
      (expect-correction-refusal! (str "bound fact missing " field)
                                  :receipt-correction/binding-fields
                                  (assoc-in fixture [:targets correction-binding-key]
                                            (dissoc (get targets correction-binding-key) field))))))

(deftest receipt-correction-requires-explicit-true-verification-fact
  (let [fixture (correction-fixture)]
    (doseq [verified [false nil 1 "true"]]
      (expect-correction-refusal! (str "binding is not explicitly verified: " (pr-str verified))
                                  :receipt-correction/binding-unverified
                                  (assoc-in fixture
                                            [:targets correction-binding-key :binding/verified?]
                                            verified)))))

(deftest receipt-correction-matches-binding-line-origin-revision-and-hash
  (let [fixture (correction-fixture)]
    (doseq [[field value code]
            [[:receipt/line 268 :receipt-correction/line-mismatch]
             [:receipt/origin "other-origin" :receipt-correction/origin-mismatch]
             [:receipt/source-revision revision-b :receipt-correction/revision-mismatch]
             [:receipt/source-sha256 (apply str (repeat 64 "b"))
              :receipt-correction/hash-mismatch]]]
      (expect-correction-refusal! (str "bound fact contradicts entry " field) code
                                  (assoc-in fixture [:targets correction-binding-key field] value)))))

(deftest receipt-correction-matches-entry-origin-to-prior-original
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (expect-correction-refusal! "entry origin differs from prior map and bound fact"
                                :receipt-correction/origin-mismatch
                                (fixture-entry fixture
                                               (assoc entry :receipt/origin "other-origin")))))

(deftest receipt-correction-matches-bound-record-to-prior-original-map
  (let [fixture (correction-fixture)]
    (expect-correction-refusal!
     "bound original differs even in a field not being corrected"
     :receipt-correction/record-mismatch
     (assoc-in fixture [:targets correction-binding-key :receipt/record :owner] "other-owner"))
    (expect-correction-refusal!
     "selected prior original differs from the verified original"
     :receipt-correction/record-mismatch
     (assoc-in fixture [:items 0 :receipt/record :evidence/result :result/exit] 7))))

(deftest receipt-correction-rejects-target-not-present-at-absolute-line
  (expect-correction-refusal! "prior item was supplied under another absolute line"
                              :receipt-correction/target-missing
                              (assoc-in (correction-fixture) [:items 0 :receipt/line] 266)))

(deftest receipt-correction-rejects-self-and-forward-targets
  (let [{:keys [entry items targets]} (correction-fixture)]
    (doseq [line [278 279]]
      (let [entry (assoc entry :receipt/line line)
            correction (documentary-correction [entry])
            binding (assoc (get targets correction-binding-key) :receipt/line line)
            selected (if (= line 278)
                       [{:receipt/line 278 :receipt/record correction}]
                       [(second (assoc items 1 {:receipt/line 278 :receipt/record correction}))
                        {:receipt/line 279 :receipt/record correction-original}])]
        (expect-correction-refusal!
         (str "target ordinal " line " is not before correction line278")
         :receipt-correction/target-not-prior
         {:items selected
          :targets {[correction-source-revision line correction-source-digest] binding}})))))

(deftest receipt-correction-rejects-duplicate-and-conflicting-targets-in-one-record
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [[scenario second-entry]
            [["identical duplicate target" entry]
             ["conflicting replacement for the same target"
              (assoc-in entry [:envelope/corrected-fields :pi] "changed")]]]
      (expect-correction-refusal!
       scenario :receipt-correction/duplicate-target
       (assoc-in fixture [:items 1 :receipt/record :correction/entries]
                 [entry second-entry])))))

(deftest receipt-correction-rejects-duplicate-targets-globally
  (let [{:keys [entry items targets]} (correction-fixture)]
    (doseq [[scenario second-entry second-targets]
            [["same target in a later correction record" entry targets]
             ["same absolute target under a different verified source identity"
              (assoc entry :receipt/source-revision revision-b)
              (assoc targets [revision-b 267 correction-source-digest]
                     (assoc (get targets correction-binding-key)
                            :receipt/source-revision revision-b))]]]
      (expect-correction-refusal!
       scenario :receipt-correction/duplicate-target
       {:items (conj items {:receipt/line 279
                            :receipt/record (documentary-correction [second-entry])})
        :targets second-targets}))))

(deftest receipt-correction-requires-nonempty-corrected-field-map
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [fields [nil {} [] "fields"]]
      (expect-correction-refusal! (str "invalid corrected-fields " (pr-str fields))
                                  :receipt-correction/corrected-fields
                                  (fixture-entry fixture
                                                 (assoc entry :envelope/corrected-fields fields))))))

(deftest receipt-correction-rejects-all-unsupported-metadata-and-semantic-fields
  (let [{:keys [entry] :as fixture} (correction-fixture)]
    (doseq [[field value]
            [[:ts "later"] [:owner "new-owner"] [:origin "new-origin"]
             [:kind :decision] [:host "other-host"] [:repo "other-repo"]
             [:evidence/schema 2] [:evidence/adapter "other-adapter"]
             [:evidence/result {:result/outcome :passed}]
             [:result/command ["true"]] [:result/outcome :passed]
             [:result/revision revision-b] [:unknown "value"]]]
      (expect-correction-refusal!
       (str "unsupported corrected field " field)
       :receipt-correction/unsupported-fields
       (fixture-entry fixture
                      (assoc-in entry [:envelope/corrected-fields field] value))))))

(deftest receipt-correction-rejects-no-op-and-replacement-of-valid-metadata
  (let [ordinary (receipt-for (recorded-result :repo/unit :passed revision-a))]
    (doseq [field [:manifest :refs :dod :pi]]
      (expect-correction-refusal!
       (str "already valid metadata cannot be corrected: " field)
       :receipt-correction/field-already-valid
       (correction-fixture ordinary {field (get ordinary field)})))
    (expect-correction-refusal!
     "a different replacement cannot rewrite an already valid DoD"
     :receipt-correction/field-already-valid
     (correction-fixture ordinary {:dod "new objective"}))))

(deftest receipt-correction-rejects-uninterpretable-original-field-types
  (let [ordinary (receipt-for (recorded-result :repo/unit :passed revision-a))]
    (doseq [[field value replacement]
            [[:manifest " " [" "]] [:refs "" [""]]
             [:manifest nil ["invented"]] [:refs :reference ["reference"]]
             [:dod [] ""] [:dod ["ok" " "] "ok;  "]
             [:dod ["ok" 42] "ok; 42"] [:dod (list "a" "b") "a; b"]
             [:pi nil "invented"] [:pi 42 "42"]]]
      (expect-correction-refusal!
       (str "unsupported original " field " value " (pr-str value))
       :receipt-correction/original-field
       (correction-fixture (assoc ordinary field value) {field replacement})))))

(deftest receipt-correction-rejects-inexact-field-interpretations
  (doseq [[field replacement]
          [[:manifest [".ημ/receipts.edn" "automation:follow-up"]]
           [:refs ["root1069" "request6044932186"]]
           [:manifest ".ημ/receipts.edn,automation:follow-up"]
           [:refs ["root1069,request6044932186" 42]]
           [:dod " first objective , second; objective"]
           [:dod "first objective; second; objective"]
           [:dod [" first objective " "second; objective"]]
           [:pi ":cephalon/character-memory"]
           [:pi "character-memory"] [:pi :cephalon/character-memory]]]
    (expect-correction-refusal!
     (str "inexact replacement for " field ": " (pr-str replacement))
     :receipt-correction/field-interpretation
     (correction-fixture correction-original {field replacement}))))

(deftest receipt-correction-invalid-ordinary-views-remain-errors
  (doseq [[scenario original]
          [["uncorrected malformed ordinary map" correction-original]
           ["ordinary map missing required origin" (dissoc correction-original :origin)]
           ["ordinary scalar" 42]]]
    (expect-correction-refusal!
     scenario :receipt/invalid-envelope
     {:items [{:receipt/line 267 :receipt/record original}] :targets {}})))

(deftest receipt-correction-partial-interpretation-does-not-hide-remaining-invalid-fields
  (let [{:keys [items entry targets]}
        (correction-fixture correction-original
                            {:manifest [".ημ/receipts.edn,automation:follow-up"]})
        result (correction-view-under-test items targets)]
    (is (contains? (set (map :error (:receipt/errors result))) :receipt/invalid-envelope)
        "refs/DoD/Pi remain invalid after a permitted manifest interpretation")
    (is (= {:receipt/originals (mapv :receipt/record items)
            :receipt/views [(assoc correction-original
                                   :manifest [".ημ/receipts.edn,automation:follow-up"])
                            (:receipt/record (second items))]
            :receipt/corrections [(assoc entry :correction/line 278)]}
           (select-keys result [:receipt/originals :receipt/views :receipt/corrections]))
        "a derived partial view is retained with its provenance and explicit envelope error")))

(deftest receipt-correction-record-itself-must-have-a-strict-envelope
  (expect-correction-refusal!
   "correction with invalid own envelope cannot admit target interpretations"
   :receipt/invalid-envelope
   (assoc-in (correction-fixture) [:items 1 :receipt/record :owner] "")))
;; END receipt-correction-view RED contract

;; BEGIN receipt-stream-view RED contract
;; These are synthetic, adapter-admitted Clojure facts. The pure composition
;; cannot authenticate raw hashes, parse raw EDN, read Git or prove ancestry.
;; Canonical identity uses :stream/repository/path/anchor/ledger-sha256;
;; source identity uses :stream/repository/path plus source base/head/hash. Items are
;; closed maps containing line/hash/raw/original/view, with absolute physical
;; ordinals. Common count is parsed records, not a physical line boundary.
;; Error maps use :error and the refusal codes asserted below. A refusal emits
;; no partial ledger or correction provenance. Identity validation must retain
;; existing canonical Git-ID compatibility; source base/head use the reviewed
;; forty-character descriptor boundary. Canonical has exactly one valid import.
;; No combined order is an occurrence key. Source common-view agreement is
;; authenticated before this delta-only interface, at the adapter boundary.
(defn receipt-stream-view-under-test []
  evidence/receipt-stream-view)

(def composition-canonical-identity
  {:stream/repository "." :stream/path ".ημ/receipts.edn"
   :stream/anchor (apply str (repeat 40 "c"))
   :stream/ledger-sha256 (apply str (repeat 64 "c"))})

(def composition-source-identity
  {:stream/repository "." :stream/path ".ημ/receipts.edn"
   :source/base revision-a :source/head revision-b
   :source/ledger-sha256 (:source/ledger-sha256 valid-receipt-stream-descriptor)})

(defn composition-record [origin]
  {:ts "2026-10-08T01:20:00Z" :kind :observation :origin origin
   :owner "pure-fixture" :dod "Preserve source occurrences" :pi "receipt-stream"
   :host "synthetic admitted facts" :manifest [] :refs []})

(defn composition-item
  ([line original] (composition-item line original original))
  ([line original view]
   {:receipt/line line :receipt/sha256 (apply str (repeat 64 "a"))
    :receipt/raw (str (pr-str original) "\n")
    :receipt/record original :receipt/view view}))

(defn composition-fixture []
  (let [original (assoc (composition-record "source-target") :manifest "exact, path")
        view (assoc original :manifest ["exact, path"])
        entry {:receipt/line 7 :receipt/origin (:origin original)
               :receipt/source-revision revision-b
               :receipt/source-sha256 (apply str (repeat 64 "a"))
               :envelope/corrected-fields {:manifest ["exact, path"]}}
        correction (assoc (composition-record "source-correction")
                          :kind :correction :correction/entries [entry])
        import (assoc (composition-record "canonical-import")
                      :kind :receipt-stream-import
                      :receipt/stream (assoc valid-receipt-stream-descriptor
                                             :source/ledger-records 4))]
    {:canonical/items [(composition-item 1 (composition-record "common-a"))
                       (composition-item 3 (composition-record "common-b"))
                       (composition-item 7 (composition-record "canonical-ordinary"))
                       (composition-item 9 import)]
     :source/items [(composition-item 7 original view)
                    (composition-item 11 correction)]
     :common/records 2
     :canonical/identity composition-canonical-identity
     :source/identity composition-source-identity
     :canonical/corrections []
     :source/corrections [(assoc entry :correction/line 11)]}))

(defn expected-composition-occurrence [role stream item]
  {:receipt/stream-role role :receipt/stream stream
   :receipt/identity [stream (:receipt/line item) (:receipt/sha256 item)]
   :receipt/line (:receipt/line item) :receipt/sha256 (:receipt/sha256 item)
   :receipt/raw (:receipt/raw item) :receipt/original (:receipt/record item)
   :receipt/view (:receipt/view item)})

(defn expect-composition-refusal! [compose scenario code facts]
  (let [result (compose facts)]
    (is (vector? (:receipt/errors result)) scenario)
    (is (contains? (set (map :error (:receipt/errors result))) code)
        (str scenario " must report " code))
    (doseq [field [:ledger/occurrences :ledger/originals :ledger/views :receipt/corrections]]
      (is (empty? (get result field))
          (str scenario " must not publish partial " field)))))

(deftest receipt-stream-view-api-must-exist
  (is (some? (receipt-stream-view-under-test))
      "Missing callable foresight.evidence/receipt-stream-view: intentional composition RED"))

(deftest receipt-stream-view-preserves-exact-occurrences-originals-views-and-counts
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          canonical (:canonical/items facts)
          imported (:source/items facts)
          originals (mapv :receipt/record (into canonical imported))
          views (mapv :receipt/view (into canonical imported))
          expected {:receipt/errors []
                    :ledger/occurrences
                    (into (mapv #(expected-composition-occurrence
                                  :canonical composition-canonical-identity %) canonical)
                          (mapv #(expected-composition-occurrence
                                  :imported composition-source-identity %) imported))
                    :ledger/originals originals :ledger/views views
                    :ledger/canonical-receipts 4 :ledger/imported-receipts 2
                    :ledger/combined-receipts 6
                    :receipt/corrections
                    (mapv #(assoc % :receipt/stream composition-source-identity)
                          (:source/corrections facts))}]
      (is (= expected (compose facts)))
      (is (= (compose facts) (compose facts)) "composition is deterministic")
      (is (= facts (composition-fixture)) "original fixture facts are unchanged")
      (is (= 6 (count (set (map :receipt/identity (:ledger/occurrences (compose facts))))))
          "equal ordinals across streams retain distinct occurrence identities"))))

(deftest receipt-stream-view-keeps-imported-occurrences-stable-after-canonical-append
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          first-view (compose facts)
          next-facts (-> facts
                         (update :canonical/items conj
                                 (composition-item 12 (composition-record "later ordinary")))
                         (assoc :canonical/identity
                                (assoc composition-canonical-identity
                                       :stream/anchor (apply str (repeat 40 "e"))
                                       :stream/ledger-sha256 (apply str (repeat 64 "f")))))
          next-view (compose next-facts)
          imported #(filterv (fn [item] (= :imported (:receipt/stream-role item)))
                             (:ledger/occurrences %))]
      (is (= [] (:receipt/errors next-view)))
      (is (= (imported first-view) (imported next-view)))
      (is (= {:ledger/canonical-receipts 5 :ledger/imported-receipts 2
              :ledger/combined-receipts 7}
             (select-keys next-view [:ledger/canonical-receipts :ledger/imported-receipts
                                    :ledger/combined-receipts])))
      (is (= (:receipt/corrections first-view) (:receipt/corrections next-view))))))

(deftest receipt-stream-view-scopes-correction-provenance-to-each-stream
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          source-entry (first (:source/corrections facts))
          canonical-original (assoc (composition-record "canonical-ordinary")
                                    :manifest "canonical, path")
          canonical-view (assoc canonical-original :manifest ["canonical, path"])
          canonical-entry (assoc source-entry :receipt/origin "canonical-ordinary"
                                 :receipt/source-revision revision-a :correction/line 8
                                 :envelope/corrected-fields {:manifest ["canonical, path"]})
          canonical-document (assoc (composition-record "canonical-correction")
                                    :kind :correction
                                    :correction/entries [(dissoc canonical-entry :correction/line)])
          facts (assoc facts :canonical/corrections [canonical-entry]
                       :canonical/items
                       (into (conj (subvec (:canonical/items facts) 0 2)
                                   (composition-item 7 canonical-original canonical-view)
                                   (composition-item 8 canonical-document))
                             [(last (:canonical/items facts))]))
          result (compose facts)]
      (is (= [] (:receipt/errors result)))
      (is (= [(assoc canonical-entry :receipt/stream composition-canonical-identity)
              (assoc source-entry :receipt/stream composition-source-identity)]
             (:receipt/corrections result)))
      (is (= [7 7] (mapv :receipt/line (:receipt/corrections result)))
          "already-admitted equal correction ordinals belong to separate streams"))))

(deftest receipt-stream-view-retains-equal-raw-source-rows-at-distinct-ordinals
  (when-let [compose (receipt-stream-view-under-test)]
    (let [item (composition-item 261 (composition-record "source repeated"))
          facts (assoc (composition-fixture)
                       :source/items [item (assoc item :receipt/line 263)]
                       :source/corrections [])
          result (compose facts)
          imported (filterv #(= :imported (:receipt/stream-role %)) (:ledger/occurrences result))]
      (is (= [] (:receipt/errors result)))
      (is (= [261 263] (mapv :receipt/line imported)))
      (is (= [(:receipt/raw item) (:receipt/raw item)] (mapv :receipt/raw imported)))
      (is (= 2 (count (set (map :receipt/identity imported)))))
      (is (= 6 (:ledger/combined-receipts result))))))

(deftest receipt-stream-view-overlap-boundary-is-common-parsed-count-not-line-number
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          common-item (second (:canonical/items facts))
          source-item (assoc common-item :receipt/line 21)
          result (compose (assoc facts :source/items [source-item] :source/corrections []))]
      (is (= [] (:receipt/errors result)))
      (is (= 5 (:ledger/combined-receipts result)))
      (is (= (expected-composition-occurrence :imported composition-source-identity source-item)
             (last (:ledger/occurrences result))))
      (is (= [1 3 7 9 21] (mapv :receipt/line (:ledger/occurrences result)))
          "blank physical lines do not consume common parsed-record count"))))

(deftest receipt-stream-view-refuses-exact-raw-overlap-after-common-records
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          canonical-item (nth (:canonical/items facts) 2)
          overlap (assoc canonical-item :receipt/line 21
                         :receipt/sha256 (apply str (repeat 64 "b")))]
      (expect-composition-refusal!
       compose "raw overlap even when supplied hashes differ" :receipt-stream/raw-overlap
       (assoc facts :source/items [overlap] :source/corrections [])))))

(deftest receipt-stream-view-does-not-deduplicate-parsed-maps-or-supplied-hashes
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          canonical-item (nth (:canonical/items facts) 2)
          source-item (assoc canonical-item :receipt/line 21
                             :receipt/raw (str " " (:receipt/raw canonical-item)))
          result (compose (assoc facts :source/items [source-item] :source/corrections []))]
      (is (= [] (:receipt/errors result)))
      (is (= 5 (:ledger/combined-receipts result)))
      (is (= (:receipt/record canonical-item) (last (:ledger/originals result))))
      (is (= (:receipt/raw source-item) (:receipt/raw (last (:ledger/occurrences result)))))
      (is (= 5 (count (set (map :receipt/identity (:ledger/occurrences result)))))))))

(deftest receipt-stream-view-allows-zero-common-records-with-one-valid-import
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (assoc (composition-fixture) :common/records 0)
          result (compose facts)]
      (is (= [] (:receipt/errors result)))
      (is (= {:ledger/canonical-receipts 4 :ledger/imported-receipts 2
              :ledger/combined-receipts 6}
             (select-keys result [:ledger/canonical-receipts :ledger/imported-receipts
                                  :ledger/combined-receipts]))))
    (expect-composition-refusal!
     compose "empty canonical journal has no required import" :receipt-stream/import-limit
     (assoc (composition-fixture) :canonical/items [] :common/records 0))))

(deftest receipt-stream-view-composes-empty-facts-without-an-import
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (assoc (composition-fixture)
                       :canonical/items [] :source/items [] :common/records 0
                       :canonical/corrections [] :source/corrections [])]
      (is (= {:receipt/errors [] :ledger/occurrences [] :ledger/originals [] :ledger/views []
              :ledger/canonical-receipts 0 :ledger/imported-receipts 0
              :ledger/combined-receipts 0 :receipt/corrections []}
             (compose facts))
          "only an empty composition needs no canonical import; identities remain shaped"))))

(deftest receipt-stream-view-refuses-malformed-input-and-common-record-count
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [facts [nil 42 []]]
      (expect-composition-refusal! compose "composition requires one Clojure map"
                                   :receipt-stream/input facts))
    (doseq [field (keys (composition-fixture))]
      (expect-composition-refusal! compose (str "missing input field " field)
                                   :receipt-stream/input (dissoc (composition-fixture) field)))
    (doseq [value [nil false "2" -1 1.5 5]]
      (expect-composition-refusal!
       compose (str "common parsed count " (pr-str value)) :receipt-stream/common-records
       (assoc (composition-fixture) :common/records value)))))

(deftest receipt-stream-view-refuses-nonvector-items-and-correction-facts
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [field [:canonical/items :source/items]
            value [nil {} '(item)]]
      (expect-composition-refusal! compose (str "nonvector " field)
                                   :receipt-stream/items (assoc (composition-fixture) field value)))
    (doseq [field [:canonical/corrections :source/corrections]
            value [nil {} '(entry) [42]]]
      (expect-composition-refusal!
       compose (str "malformed correction provenance " field) :receipt-stream/corrections
       (assoc (composition-fixture) field value)))))

(deftest receipt-stream-view-refuses-nonclosed-or-malformed-item-facts
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [role [:canonical/items :source/items]]
      (let [facts (composition-fixture)
            item (first (get facts role))
            replace-item #(assoc-in facts [role 0] %)]
        (doseq [candidate (concat
                          [nil 42 (assoc item :receipt/order 0)]
                          (map #(dissoc item %) (keys item))
                          (map #(assoc item :receipt/line %) [nil 0 -1 "1" 1.5])
                          (map #(assoc item :receipt/sha256 %)
                               [nil (apply str (repeat 40 "a")) (apply str (repeat 64 "A"))])
                          (map #(assoc item :receipt/raw %) [nil 42 "" "{}" "{}\n{}\n"])
                          [(assoc item :receipt/record []) (assoc item :receipt/view nil)
                           (assoc item :receipt/view [])])]
          (expect-composition-refusal!
           compose (str "malformed closed item in " role ": " (pr-str candidate))
           :receipt-stream/item (replace-item candidate)))))))

(deftest receipt-stream-view-refuses-duplicate-physical-lines-within-each-stream
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [role [:canonical/items :source/items]]
      (let [facts (composition-fixture)
            item (first (get facts role))]
        (expect-composition-refusal!
         compose (str "duplicate original ordinal within " role) :receipt-stream/duplicate-line
         (update facts role conj (assoc item :receipt/raw (str " " (:receipt/raw item)))))))))

(deftest receipt-stream-view-refuses-malformed-or-foreign-stream-identities
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [role [:canonical/identity :source/identity]]
      (let [facts (composition-fixture)
            identity (get facts role)
            repository :stream/repository
            path :stream/path
            digest (if (= role :canonical/identity) :stream/ledger-sha256 :source/ledger-sha256)
            revisions (if (= role :canonical/identity) [:stream/anchor] [:source/base :source/head])]
        (doseq [candidate (concat
                          [nil [] (assoc identity repository "foreign")
                           (assoc identity repository nil) (assoc identity path "./.ημ/receipts.edn")
                           (assoc identity digest (apply str (repeat 64 "A")))]
                          (map #(dissoc identity %) (keys identity))
                          (map #(assoc identity % "short-revision") revisions))]
          (expect-composition-refusal! compose (str "invalid stream identity " role)
                                       :receipt-stream/identity (assoc facts role candidate)))))))

(deftest receipt-stream-view-refuses-nested-import-triggers-in-originals-or-views
  (when-let [compose (receipt-stream-view-under-test)]
    (doseq [field [:receipt/record :receipt/view]
            record [(assoc (composition-record "nested") :kind :receipt-stream-import)
                    (assoc (composition-record "nested") :receipt/stream nil)]]
      (expect-composition-refusal!
       compose (str "source delta nested import trigger in " field) :receipt-stream/nested-import
       (assoc-in (composition-fixture) [:source/items 0 field] record)))))

(deftest receipt-stream-view-refuses-more-than-one-canonical-import
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          first-import (last (:canonical/items facts))]
      (expect-composition-refusal!
       compose "a later second canonical import cannot survive pure composition"
       :receipt-stream/import-limit
       (update facts :canonical/items conj (assoc first-import :receipt/line 12))))))

(deftest receipt-stream-view-refuses-invalid-canonical-import-triggers
  (when-let [compose (receipt-stream-view-under-test)]
    (let [facts (composition-fixture)
          import (:receipt/record (last (:canonical/items facts)))]
      (doseq [record [(dissoc import :receipt/stream)
                      (assoc import :receipt/stream nil)
                      (assoc import :kind :observation)
                      (assoc import :kind :correction)]]
        (expect-composition-refusal!
         compose "canonical trigger must be one valid native import"
         :receipt-stream/import
         (assoc-in facts [:canonical/items 3] (composition-item 9 record)))))))
;; END receipt-stream-view RED contract

(defmethod test/report [::test/default :end-run-tests] [summary]
  (set! (.-exitCode js/process) (if (test/successful? summary) 0 1)))

(test/run-tests 'evidence-test)
