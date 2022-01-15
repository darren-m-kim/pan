(ns server.handler
  (:require
   [reitit.ring :as reri]
   [reitit.swagger :as swag]
   [reitit.swagger-ui :as swui]
   [reitit.ring.middleware.muuntaja :as remu]
   [reitit.ring.middleware.exception :as rexc]
   [reitit.coercion.spec :as rspe]
   [reitit.ring.coercion :as reco]
   [muuntaja.core :as muun]
   [reitit.dev.pretty :as rpre]
   [server.middleware :as middleware]))

(defn ok [{:keys [db] :as req}]
  (println "db:" db)
  (constantly {:status 200 :body "yay"}))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "pan"}}
           :handler (swag/create-swagger-handler)}}]
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

(defn make-opts [db]
  {:exception rpre/exception
   :data {:db db
          :coercion rspe/coercion
          :muuntaja muun/instance
          :middleware [swag/swagger-feature
                       remu/format-negotiate-middleware
                       remu/format-response-middleware
                       rexc/exception-middleware
                       remu/format-request-middleware
                       reco/coerce-request-middleware
                       reco/coerce-response-middleware
                       middleware/db]}})

(defn make-router [db]
  (reri/router
   routes
   (make-opts db)))

(defn make-default-handler []
  (reri/routes
    (swui/create-swagger-ui-handler
     {:path "/"})))

(defn make-app [db]
  (reri/ring-handler
   (make-router db)
   (make-default-handler)))
