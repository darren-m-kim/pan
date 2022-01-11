(ns app.handler
  (:require
   [reitit.core :as r]
   [reitit.ring :as ring]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.exception :as exception]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [muuntaja.core :as m]
   [ring.adapter.jetty :as jetty]))

(def ok
  (constantly {:status 200 :body "ok"}))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "pan"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/comments" {:swagger {:tags ["comments"]}}
    ["" {:get {:summary "get all comments"
               :handler ok}
         :post {:summary "create a new comment"
                :parameters {:body {:name string?
                                    :slug string?
                                    :text string?
                                    :parent-comment-id int?}}
                :responses {200 {:body string?}}
                :handler ok}}]
    ["/:slug" {:get {:summary "get comments by slug"
                     :parameters {:path {:slug string?}}
                     :handler ok}}]
    ["/id/:id" {:put {:summary "update a comment by the moderator"
                      :parameters {:path {:id int?}}
                      :handler ok}
                :delete {:summary "delete a comment by the moderator"
                         :parameters {:path {:id int?}}
                         :handler ok}}]]])

(def router
  (ring/router
   routes
   {:data {:coercion reitit.coercion.spec/coercion
           :muuntaja m/instance
           :middleware [swagger/swagger-feature
                        muuntaja/format-negotiate-middleware
                        muuntaja/format-response-middleware
                        exception/exception-middleware ; order important
                        muuntaja/format-request-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-response-middleware]}}))

(def app
  (ring/ring-handler
   router
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"}))))

(defn start []
  (jetty/run-jetty
   #'app
   {:port 3000 :join? false})
  (println "server running on port 3000"))

(comment 
  (app {:request-method :get :uri "/ping"})
  (start)
  )
