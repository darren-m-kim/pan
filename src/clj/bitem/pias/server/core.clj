(ns bitem.pias.server.core
  (:require
   [clojure.spec.alpha :as s]
   [cheshire.core :as e]
   [ring.adapter.jetty :as t]
   [ring.middleware.json :as j]
   [ring.middleware.cors :as c]
   [ring.middleware.reload :as l]
   [ring.util.response :as i]
   [compojure.core :as p]
   [compojure.route :as u]
   [next.jdbc :as n]
   [next.jdbc.sql :as q]
   #_ [next.jdbc.date-time :as time]
   [java-time :as m]
   [bitem.pias.common.random :as r]
   [bitem.pias.common.shape :as h]))


;; time

(defn timestamp
  "create a sql timestamp."
  []
  (-> (m/instant)
      m/instant->sql-timestamp))


;;;; Db

(def db-info
  {:dbtype "postgres"
   :dbname "pan"
   :host "localhost"})

(defonce db
  (n/get-datasource db-info))


;;;; migration

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

(comment 
  (migrate db-conn :up)
  (migrate db-conn :down))

(defn jsonb [m]
  {:pre [(s/valid? map? m)]
   :post [(s/valid? string? %)]}
  (str "'" (e/generate-string m) "'"))

(defn insert! [db tag doc]
  (let [sql (str "insert into dt (tag, doc) values ("
                 (jsonb tag) ", "
                 (jsonb doc) ");")]
    (n/execute! db [sql])))

#_
(insert db
        {:name "darren"}
        {:age 123})

(defn insert-clientele [db m]
  {:pre [(s/valid? ::h/clientele m)]}
  (insert! db
           {:status :posted}
           m))

#_
(insert-clientele db {:name "asdf" :num-persons 345})


(def paths
  (p/routes
   (p/GET "/" [] (i/response "Bitem PIAS API Server"))
   (p/GET "/info" [] (i/response {:baz "qsssux"}))
   (p/GET "/api/clientele" [] (i/response {:name "bitem" :num-persons 3}))
   (p/POST "/api/clientele" []
           (fn [req]
             (prn (:body req))
             (i/response (assoc (:body req) :sex :group))))
   (u/not-found "Not found")))

(defn cors [next]
  (c/wrap-cors next
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get]))

(def app
  (-> paths
      (j/wrap-json-body {:keywords? true})
      (j/wrap-json-response)
      (cors)
      (l/wrap-reload)))

(defonce server
  (atom nil))

(defn jetty []
  (t/run-jetty #'app
   {:port 3548
    :join? false}))

(defn start
  "starting app on repl"
  []
  (print "jetty started.")
  (reset! server (jetty)))

(defn stop
  "stopping app on repl"
  []
  (let [s @server]
    (if s
      (do (.stop @server)
          (print "server stopped.")
          (reset! server nil))
      (print "server not running now."))))

(defn refresh []
  (stop)
  (start))

(defn run
  "point for command line"
  [_]
  (start))
