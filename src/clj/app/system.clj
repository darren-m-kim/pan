(ns app.system
  (:require
   [integrant.core :as intg]
   [ring.adapter.jetty :as jett]
   [app.handler :as handler]))

(def config
  {:comment/jetty {:handler (intg/ref :comment/handler)
                   :port 3000}
   :comment/handler {:db (intg/ref :comment/sqlite)}
   :comment/sqlite nil})

(defmethod intg/init-key :comment/jetty [_ {:keys [handler port]}]
  (println "server running on port 3000")
  (jett/run-jetty handler {:port port :join? false}))

(defmethod intg/init-key :comment/handler [_ {:keys [db]}]
  (handler/create-app db))

(defmethod intg/init-key :comment/sqlite [_ _]
  {:no-db true})

(defmethod intg/halt-key! :comment/jetty [_ jetty]
  (.stop jetty))
