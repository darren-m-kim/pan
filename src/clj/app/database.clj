(ns app.database
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]
            [app.random :as random]))

(defn kv-store [dir]
  {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
              :db-dir (io/file dir)
              :sync? true}})

(defn open! []
  (xt/start-node
   {:xtdb/tx-log (kv-store "data/dev/tx-log")
    :xtdb/document-store (kv-store "data/dev/doc-store")
    :xtdb/index-store (kv-store "data/dev/index-store")}))

(defn close! [node]
  (.close node))
