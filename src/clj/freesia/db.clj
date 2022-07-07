(ns freesia.db
  (:require
   [clojure.spec.alpha :as s]
   [cheshire.core :as e]
   [java-time :as m]
   [next.jdbc :as n]
   [next.jdbc.sql :as q]
   [freesia.shape :as h]
 #_  [bitem.pias.server.random :as r])
 #_ (:import (org.postgresql.util PGobject)))

(defn <-pgobject
  "Transform PGobject containing
  json or jsonb value to Clojure data."
  [^org.postgresql.util.PGobject v]
  (let [type  (.getType v)
        value (.getValue v)]
    (if (#{"jsonb" "json"} type)
      (when value
        (with-meta (e/parse-string value keyword)
          {:pgtype type}))
      value)))

(s/fdef now
  :ret inst?)
(defn now []
  (m/instant->sql-timestamp
   (m/instant)))

(def db-info
  {:dbtype "postgres"
   :dbname "pan"
   :host "localhost"})

(defonce db
  (n/get-datasource db-info))

(def mig-up-sql
  (str "create table data ("
       "rid serial primary key, "
       "tag jsonb not null, "
       "doc jsonb not null); "))

(def mig-down-sql
  "drop table if exists data;")

(defn migrate [db k]
  (let [sql (case k
              :up mig-up-sql
              :down mig-down-sql)]
    (n/execute! db [sql])))

(s/fdef jsonb
  :args (s/cat :m map?)
  :ret string?)
(defn jsonb [m]
  (str "'" (e/generate-string m) "'"))

(defn row->map [row]
  {:rid (-> row :data/rid)
   :tag (-> row :data/tag <-pgobject)
   :doc (-> row :data/doc <-pgobject)})

(defn read-all [subject]
  (let [sql (str "select * from data where "
                 "doc ->> 'subject' = '"
                 (name subject) "';")
        rows (q/query db [sql])]
    (->> rows
         (map row->map))))

(s/fdef read-entity-historical
  :args (s/cat :entity-id ::h/entity-id))
(defn read-entity-historical [entity-id]
  (let [sql (str "select * from data where "
                 "doc ->> 'entity-id' = '"
                 entity-id "';")
        rows (q/query db [sql])]
    (->> rows
         (map row->map))))

(s/fdef tag
  :args (s/cat :k ::h/db-act)
  :ret ::h/tag)
(defn tag [k]
  (let [now (now)]  
    (case k
      :insert {:created-at now
               :valid-from now
               :status :posted}
      :update {:created-at now :status :posted}
      :delete {:created-at now :status :posted})))

(s/fdef insert!
  :args (s/cat :tag map? :doc map?))
(defn insert! [tag doc]
  {:pre [(s/valid? map? tag)
         (s/valid? map? doc)]}
  (let [sql (str "insert into data (tag, doc) values ("
                 (jsonb tag) ", "
                 (jsonb doc) ");")]
    (n/execute! db [sql])))


(s/fdef filter-rid
  :args (s/cat :rid ::h/rid))
(defn filter-rid [rid]
  (let [sql (str "select * from data "
                 "where rid = "
                 rid ";")]
    (n/execute! db [sql])))

(s/fdef run-sql
  :args (s/cat :s string?))
(defn run-sql [s]
  (->> (n/execute! db [s])
       (map row->map)))


(comment 
  "migration"
  (migrate db :up)
  (migrate db :down))


(comment
  "snippets for testing"
  (insert! {:status :tested} {:collection :account :abilities #{1 2 3}})
  (-> (read-all :account)
      first
      :doc
      :abilities
      type))
