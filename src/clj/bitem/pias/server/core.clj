(ns bitem.pias.server.core
  (:require
   [clojure.tools.logging :as g]
   [ring.adapter.jetty :as t]
   [ring.middleware.json :as j]
   [ring.middleware.cors :as c]
   [ring.middleware.reload :as l]
   [ring.util.response :as i]
   [compojure.core :as p]
   [compojure.route :as u]
   [bitem.pias.server.content.client :as v]
   [bitem.pias.server.content.person :as o]))

(def info-handlers
  [(p/GET "/" [] (i/response "Bitem PIAS API Server"))
   (p/GET "/info" [] (i/response {:baz "qsssux"}))
   (u/not-found "Not found")])

(def paths
  (apply p/routes
         (flatten [v/client-handlers
                   o/person-handlers
                   info-handlers])))

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
