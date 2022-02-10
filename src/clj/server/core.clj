(ns server.core
  (:require
   [clojure.set :refer [join] :rename {join set-join}]
   [clojure.string :refer [join] :rename {join str-join}]
   [clojure.spec.alpha :as spec]
   [cheshire.core :as json]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-response
                                 wrap-json-body]]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.util.response :refer [response]]
   ;;[clj-http.client :as client]
   [compojure.core :refer
    [routes GET POST PUT DELETE]]
   [compojure.route :refer [not-found]]
   [next.jdbc :refer [get-datasource execute!]]
   [next.jdbc.sql :refer [insert!]]
   ;; [next.jdbc.date-time :as time]
   ;;[integrant.core :refer [ref] :rename {ref iref}]
   [shared.random :refer [uuid]]
;;   [shared.shape :as shape]
   [java-time :refer [instant instant->sql-timestamp
                      local-date sql-date plus minus]]))

(spec/def :name string?)
(spec/def :desc string?)
(spec/def :chart/entity (spec/keys :req-un [:name :desc]))


(def chart-1 {:name "Fund A Chart"
              :desc "Testing purposes"})

(spec/explain :chart/entity chart-1)

(json/generate-string chart-1)
;; => "{\"name\":\"Fund A Chart\",\"desc\":\"Testing purposes\"}"
;; "{\"chart/name\":\"Fund A Chart\",\"chart/desc\":\"Testing purposes\"}"


;; time

(defn timestamp
  "create a sql timestamp."
  []
  (-> (instant)
      instant->sql-timestamp))

;;;; Db

(def db-info
  {:dbtype "postgres"
   :dbname "pan"
   :host "localhost"})

(def db-conn
  (get-datasource db-info))

;;;; migration

(defn create-tbl-phrase [table-name]
  (str "CREATE TABLE IF NOT EXISTS " table-name " ("))

(def base-fields
  (str "recordid SERIAL PRIMARY KEY, " 
       "entityid UUID NOT NULL, "
       "createdat TIMESTAMP NOT NULL, "
       "validfrom TIMESTAMP NOT NULL, " 
       "validthru TIMESTAMP NOT NULL, "))

(def trait-field
  "trait JSONB NOT NULL, ")

(def mig-up-sql
  {:client
   (str (create-tbl-phrase 'client)
        base-fields
        trait-field
        "numperson INT NOT NULL, " 
        "nummanager INT NOT NULL);")
   :person
   (str (create-tbl-phrase 'person)
        base-fields
        trait-field
        "clienteid UUID NOT NULL, "
        "ismanager BOOLEAN NOT NULL);")})

(def mig-down-sql
  {:client "DROP TABLE IF EXISTS client;"
   :person "DROP TABLE IF EXISTS person;"})

(defn db-up [dbc]
  (doseq [sql (vals mig-up-sql)]
    (execute! dbc [sql])))

(defn db-down [dbc]
  (doseq [sql (vals mig-down-sql)]
    (execute! dbc [sql])))

(comment 
  (db-down db-conn)
  (db-up db-conn))

;;

(defn make-client [dbc legalname shortname numperson nummanager]
  (let [eid (uuid)
        now (timestamp)
        eternal (sql-date 9999)]
    {:eid eid 
     :crat now
     :vfrom now 
     :vthru eternal
     :legalname legalname
     :shortname shortname
     :numperson numperson
     :nummanager nummanager}))

(defn add-client-to-db [dbc client]
  (insert! dbc :client client))

;; (post-client db-conn "Bitem, LLC" "bitem" 2 1)

(defn get-clients [dbc]
  (execute! dbc ["select * from client;"]))

(defn get-persons [dbc]
  (execute! dbc ["select * from person;"]))

(get-clients db-conn)
;; => [#:client{:numperson 2,
;;              :crat #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :shortname "Bitem",
;;              :vthru #inst "5000-01-01T05:00:00.000000000-00:00",
;;              :aid 1,
;;              :nummanager 1,
;;              :eid #uuid "ac197719-1f37-4c2d-a689-23872896991b",
;;              :vfrom #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :legalname "Bitem, LLC"}]

(get-persons db-conn)
;; => [#:person{:vfrom #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :ismanager true,
;;              :eid #uuid "1902c205-2bc6-40b8-943b-f5b199241316",
;;              :vthru #inst "5000-01-01T05:00:00.000000000-00:00",
;;              :lastname "Kim",
;;              :aid 1,
;;              :crat #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :clienteid #uuid "ac197719-1f37-4c2d-a689-23872896991b",
;;              :firstname "Darren"}
;;     #:person{:vfrom #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :ismanager false,
;;              :eid #uuid "98d18d0b-b965-4e96-9437-ba3a281040c4",
;;              :vthru #inst "5000-01-01T05:00:00.000000000-00:00",
;;              :lastname "Lovelace",
;;              :aid 2,
;;              :crat #inst "2022-01-20T14:27:23.541000000-00:00",
;;              :clienteid #uuid "ac197719-1f37-4c2d-a689-23872896991b",
;;              :firstname "Linda"}]


;; (set-join persons clients {:client_eid :eid})

(def paths
  (routes
   (GET "/" [] (response "asdfdf"))
   (GET "/test" [] (response {:baz "qsssux"}))
   (GET "/api/clients" [] (response (get-clients)))
   (GET "/api/persons" [] (response (get-persons)))
   (not-found {:error "Not found"})))

(def app
  (-> paths
      wrap-json-body
      wrap-json-response
      (wrap-cors
       :access-control-allow-origin [#".*"]
       :access-control-allow-methods [:get])
      wrap-reload))

(defonce state
  (atom {}))

(defn jetty []
  (run-jetty #'app
   {:port 3548
    :join? false}))

(defn start
  "starting app on repl"
  []
  (let [jt (jetty)]
    (print "jetty started.")
    (swap! state #(assoc % :server jt))))

(defn stop
  "stopping app on repl"
  []
  (let [jt (:server @state)]
    (if jt
      (do (.stop jt)
          (print "jetty stopped.")
          (swap! state #(dissoc % :server)))
      (print "jetty isn't running."))))

(defn run
  "point for command line"
  [_]
  (start))
