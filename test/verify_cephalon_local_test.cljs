(ns verify-cephalon-local-test
  "Exercise diagnostic admission and exit behavior without a deployment or APIs."
  (:require [cljs.test :as test :refer [async deftest is testing]]
            [clojure.string :as str]
            [verify-cephalon-local :as diagnostic]
            ["node:crypto" :as crypto]
            ["node:fs" :as fs]
            ["node:os" :as os]
            ["node:path" :as path]
            ["node:vm" :as vm]))

(def contract-bytes "fixture contract, not a deployed resource")
(def contract-hash
  (.digest (.update (.createHash crypto "sha256") contract-bytes) "hex"))
(def manifest
  {:runtimeImageId "sha256:fixture-image"
   :files [{:path "fixture.edn" :sha256 contract-hash}]
   :operationalGaps []})

(def observed
  {:unauth 401
   :selfControlIdentity true
   :runs [{:status "completed"
           :created_at (.toISOString (js/Date.))
           :settings {:agentSpec {:eventType "schedule/ussyverse-social-creative"
                                  :scheduleId "creative"}}}]
   :schedules [{:id "ussyverse-social/creative" :rule "*/15 * * * *"}]
   :triggers [{:enabled true :events ["creative-request"]}]
   :headTools ["discord.send" "discord.read" "discord.react" "agents.spawn"]
   :makerTools ["bash" "write" "music.generate" "bluesky.publish" "discord.send"]
   :synthesisScript true
   :publicationReadOk true
   :publications [{:uri "at://fixture/image" :images 1}]
   :active []})

(defn inspect-fixture
  "Use real manifest bytes and stub only the external command boundary."
  [snapshot runtime & arguments]
  (let [directory (.mkdtempSync fs (path/join (.tmpdir os) "cephalon-verifier-"))
        commands (atom [])
        exit (atom nil)]
    (try
      (.mkdirSync fs (path/join directory "contracts"))
      (.writeFileSync fs (path/join directory "contracts" "fixture.edn") contract-bytes)
      (.writeFileSync fs (path/join directory "contracts-manifest.json")
                      (js/JSON.stringify (clj->js snapshot)))
      (let [output (with-redefs [diagnostic/deployment directory
                                diagnostic/failures (atom 0)
                                diagnostic/command!
                                (fn [executable command input]
                                  (swap! commands conj [executable command])
                                  (case (first command)
                                    "inspect" "sha256:fixture-image\n"
                                    "exec" (do
                                             (is (string? input))
                                             (js/JSON.stringify (clj->js runtime)))
                                    (throw (ex-info "Unexpected fixture command" {}))))]
                     (with-out-str
                       (reset! exit (apply diagnostic/-main arguments))))]
        {:exit @exit :commands @commands :output output})
      (finally
        (.rmSync fs directory #js {:recursive true :force true})))))

(defn inspect [snapshot runtime]
  (inspect-fixture snapshot runtime "--only" "knoxx"))

(deftest explicit-selection-before-inspection
  (doseq [arguments [[] ["--only" "other"]]]
    (let [result (apply inspect-fixture manifest observed arguments)]
      (is (= 1 (:exit result)))
      (is (empty? (:commands result))))))

(deftest reject-wrong-image-before-runtime
  (let [result (inspect (assoc manifest :runtimeImageId "sha256:wrong") observed)]
    (is (= 1 (:exit result)))
    (is (= ["inspect"] (mapv #(first (second %)) (:commands result))))))

(deftest reject-missing-or-empty-manifest-before-runtime
  (doseq [snapshot [(dissoc manifest :files)
                    (assoc manifest :files [])
                    (assoc manifest :files {})]]
    (let [result (inspect snapshot observed)]
      (is (= 1 (:exit result)))
      (is (= ["inspect"] (mapv #(first (second %)) (:commands result)))))))

(deftest reject-contract-hash-drift-before-runtime
  (let [result (inspect (assoc-in manifest [:files 0 :sha256] "wrong") observed)]
    (is (= 1 (:exit result)))
    (is (= ["inspect"] (mapv #(first (second %)) (:commands result))))))

(deftest successful-inspection-and-text-only-window
  (testing "mixed media proves a sampled image observation"
    (let [result (inspect manifest observed)]
      (is (= 0 (:exit result)))
      (is (= [["docker" ["inspect" "--format" "{{.Image}}" diagnostic/container]]
              ["docker" ["exec" "-i" diagnostic/container "node"]]]
             (:commands result)))))
  (testing "a bounded feed window cannot require image frequency"
    (doseq [publications [[{:uri "at://fixture/music-1" :images 0}
                          {:uri "at://fixture/music-2" :images 0}
                          {:uri "at://fixture/music-3" :images 0}]
                         []]]
      (let [result (inspect manifest (assoc observed :publications publications))]
        (is (= 0 (:exit result)))
        (is (re-find #"WARN No image post observed in the sampled Bluesky feed" (:output result)))))))

(deftest unavailable-publication-read-remains-failure
  (let [result (inspect manifest (assoc observed :publicationReadOk false))]
    (is (= 1 (:exit result)))
    (is (re-find #"FAIL public Bluesky feed read succeeded with a valid feed" (:output result)))))

(deftest malformed-publication-feed-remains-failure
  (let [result (inspect manifest (assoc observed :publications nil))]
    (is (= 1 (:exit result)))))

(deftest runtime-evidence-failures-return-nonzero
  (doseq [[runtime description]
          [[(assoc-in observed [:runs 0 :created_at]
                      (.toISOString (js/Date. (- (.now js/Date) (* 41 60 1000)))))
            "a persisted maker completed from the native schedule within the last40minutes"]
           [(update observed :headTools conj "bash")
            "head exposes only four conversation/delegation tools"]
           [(update observed :makerTools #(vec (remove #{"music.generate"} %)))
            "maker exposes native creation and publication tools"]
           [(assoc-in observed [:triggers 0 :enabled] false)
            "non-clock creative-request event is bound to an enabled trigger"]
           [(assoc observed :synthesisScript false)
            "the actual image contains the native music engine"]]]
    (testing description
      (let [result (inspect manifest runtime)]
        (is (= 1 (:exit result)))
        (is (str/includes? (:output result) (str "FAIL " description)))
        (is (str/includes? (:output result) "Failures: 1"))))))

(deftest creative-cadence-must-belong-to-the-selected-schedule
  (doseq [schedules [[{:id "unrelated/clock" :rule "*/15 * * * *"}]
                    [{:id "ussyverse-social/creative" :rule "*/30 * * * *"}
                     {:id "unrelated/clock" :rule "*/15 * * * *"}]]]
    (let [result (inspect manifest (assoc observed :schedules schedules))]
      (is (= 1 (:exit result)))
      (is (str/includes? (:output result)
                         "FAIL native Knoxx schedule retains the 15-minute creative cadence"))
      (is (str/includes? (:output result) "Failures: 1")))))

(defn observe-query-fixture
  "Execute the actual observation script with isolated HTTP/Mongo boundaries."
  [rows]
  (let [selected (atom rows)
        query (atom nil)
        calls (atom [])
        snapshot (atom nil)
        errors (atom [])
        cursor-ref (atom nil)
        cursor #js {:sort (fn [_]
                           (swap! calls conj :sort)
                           (swap! selected #(vec (sort-by :created_at (fn [a b] (compare b a)) %)))
                           @cursor-ref)
                    :limit (fn [n]
                             (swap! calls conj :limit)
                             (swap! selected #(vec (take n %)))
                             @cursor-ref)
                    :toArray (fn []
                               (swap! calls conj :to-array)
                               (js/Promise.resolve (clj->js @selected)))}
        collection #js {:find
                        (fn [filter _]
                          (swap! calls conj :find)
                          (let [criteria (js->clj (js/JSON.parse (js/JSON.stringify filter))
                                                  :keywordize-keys true)]
                            (reset! query criteria)
                            (swap! selected
                                   #(vec (filterv
                                          (fn [row]
                                            (every? (fn [[field value]]
                                                      (= value (get-in row (map keyword (str/split (name field) #"\.")))))
                                                    criteria)) %))))
                          cursor)}
        body-for (fn [url]
                   (cond
                     (str/includes? url "/api/admin/config/events")
                     {:runtime {:running true :triggers (:triggers observed)}
                      :control {:resources {:schedule [{:id "ussyverse-social/creative"
                                                        :resource {:rule "*/15 * * * *"}}]}}}
                     (str/includes? url "/api/auth/context") {:permissions ["agent.chat.use"]}
                     (str/includes? url "agent=ussyverse_social_replies")
                     {:tools (mapv #(hash-map :id %) (:headTools observed))}
                     (str/includes? url "agent=ussyverse_social_creative")
                     {:tools (mapv #(hash-map :id %) (:makerTools observed))}
                     (str/includes? url "/api/admin/agents/active") {:runs []}
                     (str/starts-with? url "https://public.api.bsky.app/") {:feed []}
                     :else (throw (ex-info "Unexpected fixture HTTP request" {:url url}))))
        context #js {:process #js {:env #js {:KNOXX_API_KEY "fixture-only"
                                             :KNOXX_BASE_URL "http://127.0.0.1:8000"
                                             :MONGODB_URI "fixture-only"}
                                  :exitCode 0}
                     :AbortSignal js/AbortSignal
                     :console #js {:log (fn [value] (reset! snapshot (js->clj (js/JSON.parse value) :keywordize-keys true)))
                                   :error (fn [value] (swap! errors conj value))}
                     :fetch (fn [url options]
                              (let [anonymous? (and (str/includes? url "/api/admin/config/events")
                                                    (nil? (.-headers options)))]
                                (js/Promise.resolve
                                 #js {:ok (not anonymous?) :status (if anonymous? 401 200)
                                      :json (fn [] (js/Promise.resolve (clj->js (body-for url))))})))
                     :require (fn [module]
                                (case module
                                  "mongodb" #js {:MongoClient
                                                 (fn [_]
                                                   #js {:connect (fn [] (js/Promise.resolve))
                                                        :close (fn [] (js/Promise.resolve))
                                                        :db (fn [_] #js {:collection (fn [_] collection)})})}
                                  "fs" #js {:existsSync (fn [_] true)}
                                  (throw (ex-info "Unexpected fixture module" {:module module}))))}]
    (reset! cursor-ref cursor)
    (-> (.runInNewContext vm diagnostic/observation-script context)
        (.then (fn [] {:query @query :calls @calls :snapshot @snapshot :errors @errors})))))

(deftest scheduled-run-query-filters-before-the-recent-limit
  (async done
    (let [scheduled (assoc-in (first (:runs observed)) [:settings :agentSpec :contractId]
                              "ussyverse_social_creative")
          scheduled (assoc scheduled :run_id "retained-clock")
          other (mapv (fn [n]
                        (-> scheduled
                            (assoc :run_id (str "newer-event-" n)
                                   :created_at (.toISOString (js/Date. (+ (.now js/Date) n 1000))))
                            (assoc-in [:settings :agentSpec :eventType] "cephalon/creative-request")))
                      (range 10))]
      (-> (observe-query-fixture (conj other scheduled))
          (.then (fn [{:keys [query calls snapshot errors]}]
                   (is (empty? errors))
                   (is (= {:settings.agentSpec.contractId "ussyverse_social_creative"
                           :settings.agentSpec.eventType "schedule/ussyverse-social-creative"
                           :settings.agentSpec.scheduleId "creative"} query))
                   (is (= [:find :sort :limit :to-array] calls))
                   (is (= ["retained-clock"] (mapv :run_id (:runs snapshot))))
                   (done)))
          (.catch (fn [error] (is false (ex-message error)) (done)))))))

(defmethod test/report [::test/default :end-run-tests] [summary]
  (when (pos? (+ (:fail summary) (:error summary)))
    (set! (.-exitCode js/process) 1)))

(test/run-tests 'verify-cephalon-local-test)
