(require '[clio.infra.ledger :as ledger]
         '[clio.infra.schema-store :as schema-store]
         '[clio.shape.edn :as edn]
         '[foresight.archaeology.infra :as infra]
         '["node:fs" :as fs]
         '["node:path" :as path])
(let [[mode root run] *command-line-args*
      resources-root (.join path root ".ημ/archaeology/resources")
      read-resource #(edn/read-one (.readFileSync fs (.join path resources-root %) "utf8"))
      read-ledger #(ledger/read-ledger (.join path root %))]
  (println :nbb-version "1.4.207" :mode mode :root root :run run)
  (if (= mode "target")
    (let [resource (read-resource (str run ".edn"))]
      (prn (into {} (map (fn [[role events]] [role (count events)]))
                 (infra/read-resource-ledgers read-ledger resource))))
    (let [resources (->> (.readdirSync fs resources-root)
                         (filter #(.endsWith % ".edn")) sort (mapv read-resource))
          revisions (schema-store/load-revisions (.join path root ".ημ/archaeology/schemas"))
          projection (infra/project-resources {:read-ledger read-ledger :revisions revisions}
                                              resources run)]
      (println :resources (count resources) :revisions (count revisions))
      (prn projection))))
