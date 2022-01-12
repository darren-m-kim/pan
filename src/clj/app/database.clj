(ns app.database
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]
            [app.random :as random]))

(defn start! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(def node (start!))

(defn stop! []
  (.close node))


(comment 
  (xt/submit-tx
   node
   [[::xt/put
     {:xt/id "hi2u"
      :user/name "zig"}]])

  (defn get-clients []
    (xt/q (xt/db node)
          '{:find [e]
            :where [[e :user/name "zig"]]}))

  (defn get-client-by-id [id]
    (xt/q (xt/db node)
          '{:find [e]
            :where [[e :xt/id id]]}))

  (defn get-client-by-name [client-name]
    (xt/q (xt/db node)
          '{:find [:client/name]
            :where [[e :client/name client-name]]}))

  (defn post-client [{:keys [name population] :as client}]
    (xt/submit-tx
     node
     [[::xt/put
       {:xt/id (random/make-uuid)
        :client/name name
        :client/population population}]]))

  (post-client {:name "Bitem" :population 10})
  (get-client-by-name "Bitem")
  )
