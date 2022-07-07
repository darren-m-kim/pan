(ns freesia.web
  (:require
   [clojure.tools.logging :as g]
   [ring.middleware.json :as j]
   [ring.middleware.reload :as l]
   [ring.util.response :as i]
   [clojure.java.io :as io]
   [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
   [compojure.route :as route]
   [environ.core :refer [env]]
   [ring.adapter.jetty :as jetty]
   [compojure.handler :refer [site]]
   [freesia.content.management :as v]
   [freesia.content.person :as o]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defroutes app
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))


#_
(def info-handlers
  [(p/GET "/" [] (i/response "Bitem PIAS API Server"))
   (p/GET "/info" [] (i/response {:baz "qsssux"}))
   (u/not-found "Not found")])

#_
(def paths
  (apply p/routes
         (flatten [#_v/management-handlers
                   #_o/person-handlers
                   info-handlers])))

#_
(def cors-items
  [["Access-Control-Allow-Origin" "http://localhost:1729"]
   ["Access-Control-Allow-Credentials" "true"]])

#_
(defn cors [handler]
  (fn [req]
    (let [resp (handler req)]
      (reduce (fn [r [k v]] (assoc-in r [:headers k] v)) resp cors-items))))

#_
(def app
  (-> paths
      (j/wrap-json-body {:keywords? true})
      (j/wrap-json-response)
      (cors)
      (l/wrap-reload)))

#_
(defonce server
  (atom nil))
#_
(defn jetty [port]
  (t/run-jetty #'app
   {:port port
    :join? false}))
#_
(defn start [port]
  (g/info "jetty started.")
  (reset! server (jetty port)))
#_
(defn stop []
  (let [s @server]
    (if s
      (do (.stop @server)
          (g/info "server stopped.")
          (reset! server nil))
      (g/info "server not running now."))))
#_
(defn refresh [port]
  (stop)
  (start port))
#_
(defn -main [& [port]]
  (let [port (or port 5000)]
    (start port)))
#_
(comment
  "control, will be moved to user ns."
  (start 5000)
  (stop)
  (refresh 5000))
