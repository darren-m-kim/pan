(ns bitem.pias.server.db
  (:require
   [clojure.spec.alpha :as s]
   [cheshire.core :as e]
   [java-time :as m]
   [next.jdbc :as n]
   [next.jdbc.sql :as q]
   [bitem.pias.common.shape :as h])
  (:import (java.util UUID)
           (org.postgresql.util PGobject)))

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

(defn uuid []
  (UUID/randomUUID))

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
  (str "create table if not exists dt ("
       "rid serial primary key, "
       "tag jsonb not null, "
       "doc jsonb not null);"))

(def mig-down-sql
  "drop table if exists dt;")

(defn migrate [db k]
  (let [sql (case k
              :up mig-up-sql
              :down mig-down-sql)]
    (n/execute! db [sql])))

(defn jsonb [m]
  {:pre [(s/valid? map? m)]
   :post [(s/valid? string? %)]}
  (str "'" (e/generate-string m) "'"))

(defn read-all [collection]
  {:pre [(s/valid? ::h/collection collection)]}
  (let [sql (str "select * from dt where "
                 "doc ->> 'collection' = '"
                 (name collection) "';")]
    (-> (q/query db [sql])
        first
        :dt/doc
        <-pgobject)))

(defn insert! [tag doc]
  {:pre [(s/valid? map? tag)
         (s/valid? map? doc)]}
  (let [sql (str "insert into dt (tag, doc) values ("
                 (jsonb tag) ", "
                 (jsonb doc) ");")]
    (n/execute! db [sql])))

(defn tag [k]
  {:pre [(s/valid? ::h/db-act k)]
   :post [(s/valid? ::h/tag %)]}
  (case k
    :insert {:created-at (now) :status :drafted}
    :update {:created-at (now) :status :drafted}
    :delete {:created-at (now) :status :drafted}))

(comment 
  "migration"
  (migrate db-conn :up)
  (migrate db-conn :down))
