(ns server.core
  (:require
   [clojure.set :refer [join]]
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
   ;;[integrant.core :refer [ref] :rename {ref iref}]
   [shared.random :refer [uuid]]))

(def db {:dbtype "postgres"
         :dbname "pan"})

(def ds (jdbc/get-datasource db))

(jdbc/execute! ds ["
create table client (
  id serial primary key,
  name text not null)"])

(jdbc/execute!
 ds
 ["insert into client (name) values ('Sean Corfield')"])

(jdbc/execute! ds ["select * from client;"])


(def clients
  #{{:_id "882b6098-0ac6-45cb-9722-dd0e120a5f9d"
     :eid "d09eb3a2-217f-4f93-80a3-22672fb7945b"
     :v_from "11/1/2020"
     :v_thru "12/2/2021"
     :legal_name "Bitem, LLC"
     :short_name "Bitem"}})

(def persons
  #{{:_id "2af229dc-e45a-4bdb-a15f-786b7b7b70b5"
     :eid "ac197719-1f37-4c2d-a689-23872896991b"
     :v_from "11/1/2020"
     :v_thru "12/2/2021"
     :client_eid "d09eb3a2-217f-4f93-80a3-22672fb7945b"
     :first_name "Darren"
     :last_name "Kim"}
    {:_id "842dbf58-21f6-4cf9-b04b-4d3a88630a18"
     :eid "3ae28020-0f99-4a95-ab0d-37411a438f35"
     :v_from "11/1/2020"
     :v_thru "12/2/2021"
     :client_eid "d09eb3a2-217f-4f93-80a3-22672fb7945b"
     :first_name "Jack"
     :last_name "Reacher"}})

(join persons clients {:client_eid :eid})

(def paths
  (routes
   (GET "/" [] (response "asdfdf"))
   (GET "/test" [] (response {:baz "qsssux"}))
   (GET "/api/clients" [] (response clients))
   (GET "/api/persons" [] (response persons))
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
