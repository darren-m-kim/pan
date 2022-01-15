(ns server.system
  (:require
   [integrant.core :as intg]
   [ring.adapter.jetty :as jett]
   [server.handler :as handler]
   [server.database :as database]))

(def port 3548)

(def config
  {:app/jetty {:handler (intg/ref :app/handler)
               :port port}
   :app/handler {:db (intg/ref :app/db)}
   :app/db nil})

(defmethod intg/init-key
  :app/jetty [_ {:keys [handler port]}]
  (println "server running on port " port)
  (jett/run-jetty handler {:port port
                           :join? false}))

(defmethod intg/init-key
  :app/handler [_ {:keys [db]}]
  (handler/make-app db))

(defmethod intg/init-key
  :app/db [_ _]
  (database/open!))

(defmethod intg/halt-key!
  :app/jetty [_ jetty]
  (.stop jetty))

(defmethod intg/halt-key!
  :app/db [_ db]
  (database/close! db))
