(ns server.core
  (:require
   [ring.adapter.jetty :refer [run-jetty]]
   [compojure.core :refer [routes GET POST
                           PUT DELETE]] 
   [compojure.route :refer [not-found]]
   [ring.middleware.json :refer [wrap-json-response
                                 wrap-json-body]]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.util.response :refer [response]]
   [integrant.core :refer [ref] :rename {ref iref}]))

(def all-routes
  (routes
   (GET "/" [] (response "asdfdf"))
   (GET "/test" [] (response {:baz "qsssux"}))
   (not-found {:error "Not found"})))

(def app
  (-> all-routes
      wrap-json-body
      wrap-json-response
      (wrap-cors
       :access-control-allow-origin [#".*"]
       :access-control-allow-methods [:get])))

(defn launch []
  (print "Server is running on 3548..")
  (run-jetty
   (wrap-reload #'app)
   {:port 3548
    :join? false}))

(defn run [_]
  (launch))
