;; SPDX-License-Identifier: LGPL-3.0-or-later
(ns foresight.evidence
  (:require [clojure.string :as str]))

(def gate-kinds
  #{:static :unit :integration :e2e :coverage :security :build :live-smoke
    :independent-review})

(def executions #{:local :workflow-only :external})

(def outcomes #{:passed :failed :blocked :unavailable :not-applicable})

(def receipt-ledger-path ".ημ/receipts.edn")

(def evidence-receipt-origin "revision-bound-evidence")

(def outcome-precedence
  {:failed 5
   :blocked 4
   :unavailable 3
   :not-applicable 2
   :passed 1})

(defn nonblank-string? [value]
  (and (string? value) (not (str/blank? value))))

(defn command? [value]
  (and (vector? value) (seq value) (every? nonblank-string? value)))

(defn sha256? [value]
  (and (nonblank-string? value)
       (boolean (re-matches #"[0-9a-f]{64}" value))))

(defn git-commit-id? [value]
  (and (nonblank-string? value)
       (boolean (re-matches #"(?:[0-9a-f]{64}|[0-9a-f]{40})" value))))

(defn catalog-identity? [value]
  (and (map? value)
       (nonblank-string? (:catalog/path value))
       (sha256? (:catalog/sha256 value))))

(defn source-identity? [value]
  (and (map? value)
       (nonblank-string? (:source/path value))
       (nonblank-string? (:source/repository value))
       (or (nil? (:source/revision value))
           (git-commit-id? (:source/revision value)))))

(defn gate-errors
  [repository-path gate]
  (cond-> []
    (not (keyword? (:gate/id gate)))
    (conj {:error :gate/id :repository repository-path :gate gate})

    (not (contains? gate-kinds (:gate/kind gate)))
    (conj {:error :gate/kind :repository repository-path :gate gate})

    (not (contains? executions (:gate/execution gate)))
    (conj {:error :gate/execution :repository repository-path :gate gate})

    (not (nonblank-string? (:gate/source gate)))
    (conj {:error :gate/source :repository repository-path :gate gate})

    (and (= :local (:gate/execution gate))
         (not (command? (:gate/command gate))))
    (conj {:error :gate/command :repository repository-path :gate gate})

    (and (not= :local (:gate/execution gate))
         (not (nonblank-string? (:gate/reason gate))))
    (conj {:error :gate/reason :repository repository-path :gate gate})))

(defn repository-errors
  [catalog-key repository]
  (let [path (:repository/path repository)
        gates (:repository/gates repository)]
    (into
     (cond-> []
       (not= catalog-key path)
       (conj {:error :repository/path
              :catalog-key catalog-key
              :repository/path path})

       (not (vector? gates))
       (conj {:error :repository/gates :repository catalog-key}))
     (mapcat #(gate-errors catalog-key %))
     (if (vector? gates) gates []))))

(defn duplicate-gate-ids [repositories]
  (->> repositories
       vals
       (mapcat (fn [repository]
                 (let [gates (:repository/gates repository)]
                   (if (vector? gates) gates []))))
       (keep (fn [gate]
               (let [gate-id (:gate/id gate)]
                 (when (keyword? gate-id) gate-id))))
       frequencies
       (keep (fn [[gate-id count]] (when (> count 1) gate-id)))
       sort
       vec))

(defn catalog-errors
  [catalog]
  (let [repositories (:catalog/repositories catalog)
        duplicates (when (map? repositories)
                     (duplicate-gate-ids repositories))]
    (cond-> []
      (not= 1 (:catalog/version catalog))
      (conj {:error :catalog/version :value (:catalog/version catalog)})

      (not (map? repositories))
      (conj {:error :catalog/repositories})

      (map? repositories)
      (into (mapcat (fn [[catalog-key repository]]
                      (repository-errors catalog-key repository))
                    repositories))

      (seq duplicates)
      (conj {:error :gate/id-duplicates :gate/ids duplicates}))))

(defn valid-catalog? [catalog]
  (empty? (catalog-errors catalog)))

(defn catalog-inventory-errors [catalog actionable-submodule-paths]
  (let [actionable-submodule-paths (set actionable-submodule-paths)
        repositories (:catalog/repositories catalog)]
    (if-not (map? repositories)
      []
      (->> repositories
           keys
           (remove actionable-submodule-paths)
           (sort-by pr-str)
           (mapv (fn [repository-path]
                   {:error :catalog/repository-not-actionable-submodule
                    :repository repository-path}))))))

(defn select-gates
  ([catalog repository-paths]
   (select-gates catalog repository-paths gate-kinds))
  ([catalog repository-paths kinds]
   (let [repositories (:catalog/repositories catalog)]
     (->> repository-paths
          (mapcat #(get-in repositories [% :repository/gates] []))
          (filter #(contains? kinds (:gate/kind %)))
          vec))))

(defn explicit-not-applicable? [result]
  (and (= :not-applicable (:result/outcome result))
       (nonblank-string? (:result/reason result))
       (nonblank-string? (:result/approved-by result))))

(defn exit-code? [value]
  (and (integer? value) (not (neg? value))))

(defn result-errors [result]
  (if-not (map? result)
    [{:error :result/type :result result}]
    (let [execution (:result/execution result)
          source (:result/source result)
          revision (:result/revision result)]
      (cond-> []
        (not (keyword? (:gate/id result)))
        (conj {:error :result/gate-id :result result})

        (not (contains? outcomes (:result/outcome result)))
        (conj {:error :result/outcome :result result})

        (and (= :not-applicable (:result/outcome result))
             (not (explicit-not-applicable? result)))
        (conj {:error :result/not-applicable-approval :result result})

        (not (contains? executions execution))
        (conj {:error :result/execution :result result})

        (and (= :local execution)
             (not (command? (:result/command result))))
        (conj {:error :result/command :result result})

        (and (contains? result :result/exit)
             (not (exit-code? (:result/exit result))))
        (conj {:error :result/exit :result result})

        (and (= :local execution)
             (= :passed (:result/outcome result))
             (not= 0 (:result/exit result)))
        (conj {:error :result/local-passed-exit :result result})

        (and (= :local execution)
             (not= :passed (:result/outcome result))
             (= 0 (:result/exit result)))
        (conj {:error :result/local-nonpass-exit :result result})

        (and (not= :local execution)
             (contains? result :result/exit))
        (conj {:error :result/nonlocal-exit :result result})

        (not (catalog-identity? (:result/catalog result)))
        (conj {:error :result/catalog :result result})

        (not (source-identity? source))
        (conj {:error :result/source :result result})

        (and (= :passed (:result/outcome result))
             (not (git-commit-id? revision)))
        (conj {:error :result/passed-revision :result result})

        (and (nonblank-string? revision)
             (not= revision (:source/revision source)))
        (conj {:error :result/source-revision :result result})))))

(defn valid-result? [result]
  (empty? (result-errors result)))

(defn receipt-ledger-identity? [value]
  (and (map? value)
       (= receipt-ledger-path (:ledger/path value))
       (git-commit-id? (:ledger/revision value))
       (sha256? (:ledger/sha256 value))))

(defn receipt-envelope? [receipt]
  (and (map? receipt)
       (keyword? (:kind receipt))
       (every? nonblank-string?
               ((juxt :ts :origin :owner :dod :pi :host) receipt))
       (vector? (:manifest receipt))
       (every? nonblank-string? (:manifest receipt))
       (vector? (:refs receipt))
       (every? nonblank-string? (:refs receipt))))

(def ^:private receipt-stream-descriptor-keys
  #{:source/base :source/head :source/path
    :source/ledger-sha256 :source/ledger-bytes :source/ledger-records
    :source/delta-sha256 :source/delta-bytes :source/delta-records})

(defn receipt-stream-descriptor?
  "Closed shape of one source stream. Byte, count and ancestry proof is external."
  [descriptor]
  (and (map? descriptor)
       (= receipt-stream-descriptor-keys (set (keys descriptor)))
       (every? #(and (string? %)
                     (boolean (re-matches #"[0-9a-f]{40}" %)))
               ((juxt :source/base :source/head) descriptor))
       (= receipt-ledger-path (:source/path descriptor))
       (every? sha256?
               ((juxt :source/ledger-sha256 :source/delta-sha256) descriptor))
       (every? #(and (integer? %) (pos? %))
               ((juxt :source/ledger-bytes :source/ledger-records
                      :source/delta-bytes :source/delta-records) descriptor))))

(defn receipt-stream-trigger?
  "Recognize import grammar by kind or descriptor-key presence, even if invalid."
  [receipt]
  (and (map? receipt)
       (or (= :receipt-stream-import (:kind receipt))
           (contains? receipt :receipt/stream))))

(defn receipt-stream-import?
  "Strict envelope and source descriptor shape; this predicate grants no admission."
  [receipt]
  (and (receipt-envelope? receipt)
       (= :receipt-stream-import (:kind receipt))
       (receipt-stream-descriptor? (:receipt/stream receipt))))

(def ^:private correction-envelope-fields #{:manifest :refs :dod :pi})

(def ^:private correction-entry-keys
  #{:receipt/line :receipt/origin :receipt/source-revision
    :receipt/source-sha256 :envelope/corrected-fields})

(def ^:private correction-binding-keys
  [:receipt/line :receipt/origin :receipt/source-revision
   :receipt/source-sha256 :receipt/record])

(defn- valid-envelope-field? [field value]
  (case field
    (:manifest :refs) (and (vector? value) (every? nonblank-string? value))
    (:dod :pi) (nonblank-string? value)
    false))

(defn- interpreted-envelope-field [field value]
  (case field
    (:manifest :refs) (when (nonblank-string? value) [value])
    :dod (when (and (vector? value) (seq value)
                    (every? nonblank-string? value))
           (str/join "; " value))
    :pi (when (keyword? value)
          (let [text (subs (str value) 1)]
            (when (nonblank-string? text) text)))
    nil))

(defn- correction-error [code context]
  (assoc context :error code))

(defn- correction-field-errors [original fields context]
  (into []
        (keep (fn [field]
                (let [value (get original field)
                      interpretation (interpreted-envelope-field field value)
                      replacement (get fields field)
                      code (cond
                             (valid-envelope-field? field value)
                             :receipt-correction/field-already-valid

                             (nil? interpretation)
                             :receipt-correction/original-field

                             (not (and (valid-envelope-field? field replacement)
                                       (= interpretation replacement)))
                             :receipt-correction/field-interpretation)]
                  (when code
                    (correction-error code (assoc context :field field))))))
        (sort (keys fields))))

(defn- correction-entry-errors
  [entry correction-line correction-index target-index duplicates verified-targets]
  (let [line (:receipt/line entry)
        origin (:receipt/origin entry)
        revision (:receipt/source-revision entry)
        digest (:receipt/source-sha256 entry)
        fields (:envelope/corrected-fields entry)
        target (get target-index line)
        original (get-in target [:item :receipt/record])
        binding-key [revision line digest]
        binding (get verified-targets binding-key)
        context {:correction/line correction-line :receipt/line line}
        code (cond
               (not (map? entry)) :receipt-correction/entry-type
               (not= correction-entry-keys (set (keys entry)))
               :receipt-correction/entry-fields
               (not (and (integer? line) (pos? line)))
               :receipt-correction/target-line
               (not (nonblank-string? origin)) :receipt-correction/entry-origin
               (not (git-commit-id? revision)) :receipt-correction/source-revision
               (not (sha256? digest)) :receipt-correction/source-sha256
               (not (and (map? fields) (seq fields)))
               :receipt-correction/corrected-fields
               (not (every? correction-envelope-fields (keys fields)))
               :receipt-correction/unsupported-fields
               (not (< line correction-line)) :receipt-correction/target-not-prior
               (nil? target) :receipt-correction/target-missing
               (not (< (:index target) correction-index))
               :receipt-correction/target-not-prior
               (contains? duplicates line) :receipt-correction/duplicate-target
               (not (contains? verified-targets binding-key))
               :receipt-correction/binding-missing
               (not (map? binding)) :receipt-correction/binding-type
               (not (every? #(contains? binding %) correction-binding-keys))
               :receipt-correction/binding-fields
               (not (true? (:binding/verified? binding)))
               :receipt-correction/binding-unverified
               (not= line (:receipt/line binding)) :receipt-correction/line-mismatch
               (not (and (= origin (:receipt/origin binding))
                         (= origin (:origin original))))
               :receipt-correction/origin-mismatch
               (not= revision (:receipt/source-revision binding))
               :receipt-correction/revision-mismatch
               (not= digest (:receipt/source-sha256 binding))
               :receipt-correction/hash-mismatch
               (not (and (map? original) (map? (:receipt/record binding))
                         (= original (:receipt/record binding))))
               :receipt-correction/record-mismatch)]
    (if code
      [(correction-error code context)]
      (correction-field-errors original fields context))))

(defn- correction-documents [items]
  (into []
        (keep-indexed
         (fn [index item]
           (let [record (:receipt/record item)]
             (when (and (map? record) (contains? record :correction/entries))
               {:index index :line (:receipt/line item) :record record}))))
        items))

(defn- duplicate-correction-targets [documents]
  ;; Preflight all claims before deriving any view. A later duplicate also
  ;; refuses the first claim, even when its source identity differs.
  (->> documents
       (mapcat (fn [{:keys [record]}]
                 (let [entries (:correction/entries record)]
                   (if (vector? entries) entries []))))
       (keep (fn [entry]
               (when (map? entry)
                 (let [line (:receipt/line entry)]
                   (when (and (integer? line) (pos? line)) line)))))
       frequencies
       (keep (fn [[line claims]] (when (> claims 1) line)))
       set))

(defn- apply-correction-document
  [state {:keys [index line record]} target-index duplicates verified-targets]
  (let [entries (:correction/entries record)
        code (cond
               (not= :correction (:kind record)) :receipt-correction/kind
               (not (receipt-envelope? record)) :receipt/invalid-envelope
               (not (and (vector? entries) (seq entries)))
               :receipt-correction/entries)]
    (if code
      (update state :receipt/errors conj
              (correction-error code {:receipt/line line}))
      (reduce
       (fn [current entry]
         (let [errors (correction-entry-errors
                       entry line index target-index duplicates verified-targets)]
           (if (seq errors)
             (update current :receipt/errors into errors)
             ;; All fields must qualify before a single field is applied.
             (-> current
                 (update-in [:receipt/views
                             (:index (get target-index (:receipt/line entry)))]
                            merge (:envelope/corrected-fields entry))
                 (update :receipt/corrections conj
                         (assoc entry :correction/line line))))))
       state entries))))

(defn receipt-correction-view
  "Derive documentary envelope views from original, absolutely numbered items.

  verified-targets contains adapter-supplied facts keyed by
  [full-source-revision absolute-line original-including-LF-SHA256]. This pure
  decision matches those facts; it performs no Git, byte/hash or ancestry
  verification. Originals and evidence payloads are retained unchanged.
  Errors refuse ledger admission, including remaining malformed partial views.
  Successful provenance is the documentary entry plus :correction/line."
  [items verified-targets]
  (let [selected (if (vector? items) items [])
        originals (mapv :receipt/record selected)
        item-lines (mapv :receipt/line selected)
        item-errors (cond-> []
                      (not (vector? items))
                      (conj {:error :receipt/items-type})
                      (not (every? #(and (map? %)
                                        (contains? % :receipt/record)
                                        (integer? (:receipt/line %))
                                        (pos? (:receipt/line %))) selected))
                      (conj {:error :receipt/items-shape})
                      (not= (count item-lines) (count (set item-lines)))
                      (conj {:error :receipt/item-line-duplicate}))
        initial {:receipt/originals originals
                 :receipt/views originals
                 :receipt/corrections []
                 :receipt/errors item-errors}
        derived (cond
                  (seq item-errors) initial
                  (not (map? verified-targets))
                  (update initial :receipt/errors conj
                          {:error :receipt-correction/bindings-type})
                  :else
                  (let [documents (correction-documents selected)
                        duplicates (duplicate-correction-targets documents)
                        target-index (into {}
                                           (map-indexed
                                            (fn [index item]
                                              [(:receipt/line item)
                                               {:index index :item item}]))
                                           selected)]
                    (reduce #(apply-correction-document
                              %1 %2 target-index duplicates verified-targets)
                            initial documents)))]
    ;; Check envelopes after derivation, without duplicating evidence/result
    ;; validation. A partial permitted interpretation is never an implicit pass.
    (update derived :receipt/errors into
            (keep-indexed
             (fn [index view]
               (when-not (receipt-envelope? view)
                 {:error :receipt/invalid-envelope
                  :receipt/line (:receipt/line (get selected index))}))
             (:receipt/views derived)))))

(def ^:private receipt-stream-input-keys
  #{:canonical/items :source/items :common/records :canonical/identity
    :source/identity :canonical/corrections :source/corrections})

(def ^:private receipt-stream-item-keys
  #{:receipt/line :receipt/sha256 :receipt/raw :receipt/record :receipt/view})

(defn- receipt-stream-identity? [role value]
  (and (map? value)
       (= "." (:stream/repository value))
       (= receipt-ledger-path (:stream/path value))
       (if (= :canonical role)
         (and (= #{:stream/repository :stream/path :stream/anchor :stream/ledger-sha256}
                 (set (keys value)))
              (git-commit-id? (:stream/anchor value))
              (sha256? (:stream/ledger-sha256 value)))
         (and (= #{:stream/repository :stream/path :source/base :source/head
                   :source/ledger-sha256}
                 (set (keys value)))
              (every? #(and (string? %)
                            (boolean (re-matches #"[0-9a-f]{40}" %)))
                      ((juxt :source/base :source/head) value))
              (sha256? (:source/ledger-sha256 value))))))

(defn- receipt-stream-item? [item]
  (and (map? item)
       (= receipt-stream-item-keys (set (keys item)))
       (integer? (:receipt/line item))
       (pos? (:receipt/line item))
       (sha256? (:receipt/sha256 item))
       (let [raw (:receipt/raw item)]
         (and (nonblank-string? raw)
              (str/ends-with? raw "\n")
              (not (str/includes? (subs raw 0 (dec (count raw))) "\n"))))
       (map? (:receipt/record item))
       (map? (:receipt/view item))))

(defn- receipt-stream-fact-errors [streams common-records]
  (let [canonical-items (:items (first streams))
        common-valid? (and (integer? common-records) (not (neg? common-records))
                           (or (not (vector? canonical-items))
                               (<= common-records (count canonical-items))))]
    (reduce
     (fn [errors {:keys [role items identity corrections]}]
       (cond-> errors
         (not (vector? items))
         (conj {:error :receipt-stream/items :receipt/stream-role role})

         (vector? items)
         (into (keep-indexed
                (fn [index item]
                  (when-not (receipt-stream-item? item)
                    {:error :receipt-stream/item :receipt/stream-role role
                     :receipt/index index}))
                items))

         (not (receipt-stream-identity? role identity))
         (conj {:error :receipt-stream/identity :receipt/stream-role role})

         (not (and (vector? corrections) (every? map? corrections)))
         (conj {:error :receipt-stream/corrections :receipt/stream-role role})))
     (if common-valid? [] [{:error :receipt-stream/common-records}])
     streams)))

(defn- receipt-stream-composition-errors [streams common-records]
  (let [canonical (:items (first streams))
        source (:items (second streams))
        empty-composition? (every? #(and (empty? (:items %))
                                         (empty? (:corrections %))) streams)
        triggers (filterv #(or (receipt-stream-trigger? (:receipt/record %))
                               (receipt-stream-trigger? (:receipt/view %))) canonical)
        non-common-raw (set (map :receipt/raw (drop common-records canonical)))]
    (into
     (cond-> []
       (and (not empty-composition?) (not= 1 (count triggers)))
       (conj {:error :receipt-stream/import-limit})

       (and (= 1 (count triggers))
            (not (and (receipt-stream-import? (:receipt/record (first triggers)))
                      (receipt-stream-import? (:receipt/view (first triggers))))))
       (conj {:error :receipt-stream/import}))
     (concat
      (mapcat
       (fn [{:keys [role items]}]
         (keep (fn [[line occurrences]]
                 (when (> occurrences 1)
                   {:error :receipt-stream/duplicate-line :receipt/stream-role role
                    :receipt/line line}))
               (sort-by key (frequencies (map :receipt/line items)))))
       streams)
      (mapcat
       (fn [item]
         (cond-> []
           (or (receipt-stream-trigger? (:receipt/record item))
               (receipt-stream-trigger? (:receipt/view item)))
           (conj {:error :receipt-stream/nested-import :receipt/line (:receipt/line item)})

           (contains? non-common-raw (:receipt/raw item))
           (conj {:error :receipt-stream/raw-overlap :receipt/line (:receipt/line item)})))
       source)))))

(defn- receipt-stream-occurrence [role stream item]
  {:receipt/stream-role role :receipt/stream stream
   :receipt/identity [stream (:receipt/line item) (:receipt/sha256 item)]
   :receipt/line (:receipt/line item) :receipt/sha256 (:receipt/sha256 item)
   :receipt/raw (:receipt/raw item) :receipt/original (:receipt/record item)
   :receipt/view (:receipt/view item)})

(defn receipt-stream-view
  "Compose one canonical journal and its adapter-admitted source delta.

  All items and correction entries are supplied admitted facts. This decision
  checks portable boundaries and refuses overlap or nested imports atomically;
  it does not parse raw EDN, authenticate hashes/Git ancestry, revalidate ordinary
  envelopes/results or establish source common-view agreement. Those proofs
  remain with the existing adapter. Imported identity retains original stream
  coordinates and has no combined position, so a canonical append leaves it stable.
  Empty item/correction vectors compose without an import; all fact shapes still apply."
  [facts]
  (let [input-valid? (and (map? facts) (= receipt-stream-input-keys (set (keys facts))))
        streams (when input-valid?
                  [{:role :canonical :items (:canonical/items facts)
                    :identity (:canonical/identity facts) :corrections (:canonical/corrections facts)}
                   {:role :imported :items (:source/items facts)
                    :identity (:source/identity facts) :corrections (:source/corrections facts)}])
        common-records (:common/records (when input-valid? facts))
        fact-errors (if input-valid?
                      (receipt-stream-fact-errors streams common-records)
                      [{:error :receipt-stream/input}])
        errors (if (seq fact-errors)
                 fact-errors
                 (receipt-stream-composition-errors streams common-records))]
    (if (seq errors)
      {:receipt/errors errors :ledger/occurrences [] :ledger/originals []
       :ledger/views [] :receipt/corrections []}
      (let [canonical (:items (first streams))
            source (:items (second streams))
            items (into canonical source)]
        {:receipt/errors []
         :ledger/occurrences
         (into [] (mapcat (fn [{:keys [role identity items]}]
                           (map #(receipt-stream-occurrence role identity %) items))) streams)
         :ledger/originals (mapv :receipt/record items)
         :ledger/views (mapv :receipt/view items)
         :ledger/canonical-receipts (count canonical)
         :ledger/imported-receipts (count source)
         :ledger/combined-receipts (count items)
         :receipt/corrections
         (into [] (mapcat (fn [{:keys [identity corrections]}]
                           (map #(assoc % :receipt/stream identity) corrections))) streams)}))))

(defn evidence-receipt? [receipt]
  (let [schema (:evidence/schema receipt)]
    (and (receipt-envelope? receipt)
         (= :test-run (:kind receipt))
         (= evidence-receipt-origin (:origin receipt))
         (or (nil? schema)
             (and (= 2 schema)
                  (nonblank-string? (:evidence/adapter receipt))))
         (valid-result? (:evidence/result receipt)))))

(defn immutable-receipt-ledger? [ledger]
  (and (map? ledger)
       (receipt-ledger-identity? (:ledger/identity ledger))
       (vector? (:ledger/records ledger))))

(defn receipt-attests-result? [receipt result]
  (let [source (:result/source result)
        expected-ref (str (:source/repository source)
                          "@"
                          (:result/revision result))]
    (boolean
     (and (evidence-receipt? receipt)
          (= 2 (:evidence/schema receipt))
          (nonblank-string? (:evidence/adapter receipt))
          (= result (:evidence/result receipt))
          (some #{(:catalog/path (:result/catalog result))}
                (:manifest receipt))
          (some #{(:source/path source)} (:manifest receipt))
          (some #{expected-ref} (:refs receipt))
          (some #{(str (:gate/id result))} (:refs receipt))))))

(defn satisfied? [result]
  (or (= :passed (:result/outcome result))
      (explicit-not-applicable? result)))

(defn promotion-satisfied? [result]
  (= :passed (:result/outcome result)))

(defn automatic-promotion-supported? [gate]
  ;; Coverage needs a retained report, threshold, and baseline attestation.
  ;; Until that versioned contract lands, a zero exit must fail closed here.
  (not= :coverage (:gate/kind gate)))

(defn gate-index [catalog]
  (let [repositories (:catalog/repositories catalog)]
    (if-not (map? repositories)
      {}
      (into {}
            (mapcat
             (fn [[repository-path repository]]
               (let [gates (:repository/gates repository)]
                 (map (fn [gate]
                        [(:gate/id gate)
                         {:repository/path repository-path
                          :gate gate}])
                      (if (vector? gates) gates [])))))
            repositories))))

(defn result-matches-gate?
  [catalog-identity target-revision result
   {:keys [repository/path gate]}]
  (and (= catalog-identity (:result/catalog result))
       (= (:gate/execution gate) (:result/execution result))
       (= (:gate/command gate) (:result/command result))
       (= {:source/path (:gate/source gate)
           :source/repository path
           :source/revision target-revision}
          (:result/source result))))

(defn summarize-results [results]
  (if-not (coll? results)
    {:result/outcome :failed
     :result/counts {}
     :result/satisfied? false
     :result/errors [{:error :results/type :results results}]}
    (let [errors (vec (mapcat result-errors results))]
      (if (seq errors)
        {:result/outcome :failed
         :result/counts (->> results
                             (map :result/outcome)
                             (filter outcomes)
                             frequencies
                             (into (sorted-map)))
         :result/satisfied? false
         :result/errors errors}
        (let [counts (frequencies (map :result/outcome results))
              overall (if (seq results)
                        (->> results
                             (map :result/outcome)
                             (apply max-key outcome-precedence))
                        :unavailable)]
          {:result/outcome overall
           :result/counts (into (sorted-map) counts)
           :result/satisfied? (boolean
                               (and (seq results)
                                    (every? satisfied? results)))})))))

(defn promotion-evidence-consistent?
  "Pure consistency over records authenticated by an effectful boundary.
  This predicate is not promotion authority."
  [catalog catalog-identity target-revision required-gate-ids results
   immutable-receipt-ledger]
  (if-not (and (coll? required-gate-ids)
               (coll? results)
               (immutable-receipt-ledger? immutable-receipt-ledger))
    false
    (let [required-gate-ids (set required-gate-ids)
          required-results (filterv #(contains? required-gate-ids (:gate/id %))
                                    results)
          gate-id-counts (frequencies (map :gate/id required-results))
          by-id (into {} (map (juxt :gate/id identity)) required-results)
          trusted-gates (gate-index catalog)
          receipts (:ledger/records immutable-receipt-ledger)]
      (boolean
       (and (valid-catalog? catalog)
            (catalog-identity? catalog-identity)
            (git-commit-id? target-revision)
            (seq required-gate-ids)
            (every? #(contains? trusted-gates %) required-gate-ids)
            (every? #(= 1 (get gate-id-counts % 0)) required-gate-ids)
            (every? (fn [gate-id]
                      (when-let [result (get by-id gate-id)]
                        (and (valid-result? result)
                             (promotion-satisfied? result)
                             (automatic-promotion-supported?
                              (:gate (get trusted-gates gate-id)))
                             (= target-revision (:result/revision result))
                             (some #(receipt-attests-result? % result)
                                   receipts)
                             (result-matches-gate?
                              catalog-identity
                              target-revision
                              result
                              (get trusted-gates gate-id)))))
                    required-gate-ids))))))
