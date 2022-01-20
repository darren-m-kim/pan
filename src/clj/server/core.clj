(ns server.core
  (:require
   [clojure.set :refer [join]
    :rename {join set-join}]
   [clojure.string :refer [join]
    :rename {join str-join}]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer
    [wrap-json-response wrap-json-body]]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.util.response :refer [response]]
   ;;[clj-http.client :as client]
   [compojure.core :refer
    [routes GET POST PUT DELETE]]
   [compojure.route :refer [not-found]]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.date-time :as time]
   ;;[integrant.core :refer [ref] :rename {ref iref}]
   [shared.random :refer [uuid]]
   [java-time :as jt]))




;; time

(defn make-date-zoned [{:keys [year month day]}]
  (jt/zoned-date-time year month day))


(defn make-years [n]
  (jt/years n))

(defn make-months [n]
  (jt/months n))

(defn make-days [n]
  (jt/days n))

(defn make-date [{:keys [year month day]}]
  (jt/local-date year month day))

(jt/local-date-time 2021 4 12 13 23 31 123)


(defn sqldate->localdate [sqldate]
  (jt/local-date sqldate))

(defn make-stamp []
  (jt/instant->sql-timestamp 
   (jt/instant)))

(defn str->date [s]
  (jt/local-date "yyyy-MM-dd" s))

(defn date->str [d]
  (jt/format "yyyy-MM-dd" d))

(defn ensure-date-type [x]
  (if (string? x)
    (str->date x)
    x))

(defn ensure-date-string [x]
  (if (string? x)
    x (date->str x)
    ))

(defn add [{:keys [date span]}]
  (jt/plus date span))

(defn subtract [{:keys [date span]}]
  (jt/minus date span))

(defn until [{:keys [earlier later]}]
  (let [ens-earlier (ensure-date-type earlier)
        ens-later (ensure-date-type later)]
    (.until ens-earlier ens-later (java.time.temporal.ChronoUnit/DAYS))))

(defn later? [earlier later]
  (> (until {:earlier earlier :later later}) 0))

;;;; Db

(def db-info
  {:dbtype "postgres"
   :dbname "pan"
   :host "localhost"})

(def db-conn
  (jdbc/get-datasource db-info))

(defn convert-symbol [symb]
  (case (name symb)
    "p-open" "("
    "p-close" ")"
    "comma" ","
    "semicolon" ";"
    symb))

(defn sqlize [vec]
  (->> vec
       (map convert-symbol)
       (str-join " ")))


;;;; migration

(def mig-up-sql
  {:client 
   (sqlize ['create 'table 'if 'not 'exists 'client
            'p-open 'aid 'serial 'primary 'key
            'comma 'eid 'uuid 'not 'null
            'comma 'v_from 'timestamp 'not 'null
            'comma 'v_thru 'timestamp 'not 'null
            'comma 'legal_name 'text 'not 'null
            'comma 'short_name 'text 'not 'null
            'comma 'num_person 'int 'not 'null
            'comma 'num_manager 'int 'not 'null
            'p-close 'semicolon])
   :person
   (sqlize ['create 'table 'if 'not 'exists 'person
            'p-open 'aid 'serial 'primary 'key
            'comma 'eid 'uuid 'not 'null
            'comma 'v_from 'timestamp 'not 'null
            'comma 'v_thru 'timestamp 'not 'null
            'comma 'client_id 'uuid 'not 'null
            'comma 'first_name 'text 'not 'null
            'comma 'last_name 'text 'not 'null
            'comma 'is_manager 'boolean 'not 'null
            'p-close 'semicolon])})

(def mig-down-sql
  {:client
   (sqlize ['drop 'table 'if 'exists 'client 'semicolon])
   :person
   (sqlize ['drop 'table 'if 'exists 'person 'semicolon])})

(defn db-up []
  (doseq [sql (vals mig-up-sql)]
    (jdbc/execute! db-conn [sql])))

(defn db-down []
  (doseq [sql (vals mig-down-sql)]
    (jdbc/execute! db-conn [sql])))

;; (db-up)
;; (db-down)

;;;;;;;;;;

(def clients
  #{{:eid #uuid "ac197719-1f37-4c2d-a689-23872896991b"
     :v_from (make-stamp)
     :v_thru (make-stamp)
     :legal_name "Bitem, LLC"
     :short_name "Bitem"
     :num_person 2
     :num_manager 1}})

(def persons
[{:eid   #uuid "1902c205-2bc6-40b8-943b-f5b199241316" 
     :v_from (make-stamp);; => #inst "2022-01-20T06:59:31.627826000-00:00"
     :v_thru #inst "5000-01-20T06:59:31.627826000-00:00"
     :client_id #uuid "ac197719-1f37-4c2d-a689-23872896991b"
     :first_name "Darren"
     :last_name "Kim"
     :is_manager true}
    {:eid #uuid "3ae28020-0f99-4a95-ab0d-37411a438f35"
     :v_from (make-stamp)
     :v_thru #inst "5000-01-20T06:59:31.627826000-00:00"
     :client_id #uuid "ac197719-1f37-4c2d-a689-23872896991b"
     :first_name "Jack"
     :last_name "Reacher"
     :is_manager false}])

(sql/insert! db-conn :client (first clients))

(sql/insert! db-conn :person (first persons))
(sql/insert! db-conn :person (second persons))


(defn get-clients []
  (jdbc/execute! db-conn ["select * from client;"]))

(defn get-persons []
  (jdbc/execute! db-conn ["select * from person;"]))



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
