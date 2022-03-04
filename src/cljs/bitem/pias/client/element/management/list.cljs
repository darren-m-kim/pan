(ns bitem.pias.client.element.management.list
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   #_[clojure.spec.alpha :as s]
   #_[bitem.pias.common.shape :as h]
   [cljs-http.client :as http]
   [bitem.pias.client.state :as t]
   [cljs.core.async :refer [<!]]
   [bitem.pias.client.element.management.edit :as e]
   ))

(defonce d (atom []))

(defn fetch []
  (go (let [response (<! (http/get "http://localhost:3548/api/management"))
            managements (-> response :body)]
        (reset! d managements))))


(defn table []
  (fetch)
  [:table
   [:thead
    [:tr
     [:th "entity-id"]
     [:th "user-hash"]
     [:th "name"]
     [:th "num-of-users"]
     [:th "action"]]]
   [:tbody
    (map (fn [m]
           [:tr
            [:td (-> m :doc :entity-id)]
            [:td (-> m :doc :user-hash)]
            [:td (-> m :doc :name)]
            [:td (-> m :doc :num-persons)]
            [:td {:on-click
                  (fn []
                    (reset! e/entity m)
                    (reset! t/element
                            [:management :edit]))}
             "edit"]])
         @d)]])
