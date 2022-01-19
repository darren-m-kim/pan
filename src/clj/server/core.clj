(ns server.core
  (:require
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
   ;;[integrant.core :refer [ref] :rename {ref iref}]
   [shared.random :refer [uuid]]))

(def clients
  [{:_id "882b6098-0ac6-45cb-9722-dd0e120a5f9d"
    :ed "d09eb3a2-217f-4f93-80a3-22672fb7945b"
    :v_from "11/1/2020"
    :v_thru "12/2/2021"
    :legal_name "Bitem, LLC"
    :short_name "Bitem"}])

;; (uuid)

(def workers
  [{:_id "2af229dc-e45a-4bdb-a15f-786b7b7b70b5"
    :ed "ac197719-1f37-4c2d-a689-23872896991b"
    :v_from "11/1/2020"
    :v_thru "12/2/2021"
    :client_id "d09eb3a2-217f-4f93-80a3-22672fb7945b"
    :first_name "Darren"
    :last_name "Kim"}])

(def paths
  (routes
   (GET "/" [] (response "asdfdf"))
   (GET "/test" [] (response {:baz "qsssux"}))
   (GET "/api/client" [] (response clients))
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
