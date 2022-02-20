(ns bitem.pias.client.element.management-list
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [clojure.spec.alpha :as s]
   [reagent.core :as r]
   [bitem.pias.common.shape :as h]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

(defonce mgmt-list (atom []))

(defn fetch []
  (go (let [response (<! (http/get "http://localhost:3548/api/management"))
            managements (-> response :body)]
        (prn "%%%%" managements)
        (reset! mgmt-list managements))))

(fetch)

(defn table []
  [:table {:class :styled-table}
   [:thead
    [:tr
     [:th "Management Name"]
     [:th "Number of End Users"]]]
   [:tbody
    (map (fn [m] [:tr
                  [:td (-> m :doc :name)]
                  [:td (-> m :doc :num-persons)]
                  [:td [:button "Edit"]]]) @mgmt-list)]])
