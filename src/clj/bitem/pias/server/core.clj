(ns bitem.pias.server.core
  (:require
   [clojure.tools.logging :as g]
   [ring.adapter.jetty :as t]
   [ring.middleware.json :as j]
   [ring.middleware.reload :as l]
   [ring.util.response :as i]
   [compojure.core :as p]
   [compojure.route :as u]
   [bitem.pias.server.content.management :as v]
   [bitem.pias.server.content.person :as o]))

(def info-handlers
  [(p/GET "/" [] (i/response "Bitem PIAS API Server"))
   (p/GET "/info" [] (i/response {:baz "qsssux"}))
   (u/not-found "Not found")])

(def paths
  (apply p/routes
         (flatten [v/management-handlers
                   o/person-handlers
                   info-handlers])))

(def cors-items
  [["Access-Control-Allow-Origin" "http://localhost:1729"]
   ["Access-Control-Allow-Credentials" "true"]])

(defn cors [handler]
  (fn [req]
    (let [resp (handler req)]
      (reduce (fn [r [k v]] (assoc-in r [:headers k] v)) resp cors-items))))

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

(defn start []
  (g/info "jetty started.")
  (reset! server (jetty)))

(defn stop []
  (let [s @server]
    (if s
      (do (.stop @server)
          (g/info "server stopped.")
          (reset! server nil))
      (g/info "server not running now."))))

(defn refresh []
  (stop)
  (start))

(defn run [_]
  (start))

(comment
  "control, will be moved to user ns."
  (start)
  (stop)
  (refresh))
