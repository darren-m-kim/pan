(ns bitem.pias.client.core 
  (:require
   [clojure.spec.alpha :as s]
   [reagent.core :as r]
   [reagent.dom :as d]
   [bitem.pias.common.shape :as h]
   [bitem.pias.client.state :as t]
   [bitem.pias.client.element.management.list :as m]
   [bitem.pias.client.element.management.edit :as e]))

(defn menu []
  [:div
   [:p "menu"]
   [:ul
    [:li
     [:a "management"]
     [:ul
      [:li [:a {:on-click #(reset! t/element [:management :list])} "list-managements"]]
      [:li [:a {:on-click #(reset! t/element [:management :edit])} "edit-management"]]
      [:li [:a {:on-click #(reset! t/element [:management :add])} "add-management"]]]]
    [:li
     [:a "account"]
     [:ul
      [:li [:a {:on-click (fn [] (reset! t/element :all-charts))} "all-charts"]]
      [:li [:a "add-chart"]]
      [:li [:a "edit-chart"]]
      [:li [:a "all-accounts-of-a-chart"]]
      [:li [:a "add-account-to-a-chart"]]
      [:li [:a "edit-account"]]]]
    [:li [:a "entity"]]
    [:li [:a "time"]]
    [:li [:a "out"]]]])

(defn status-liner [elem]
  (let [[e1 e2] (map name elem)]
    (str e1 "->" e2)))

(defn content []
  [:div
   [:p (status-liner @t/element)]
   (case @t/element
     [:management :list] [m/table]
     [:management :edit] [e/ttt]
     [:management :add] [m/table]
     [:p "no element is selected."])])

(defn hub []
  [:div
   [menu]
   [content]])

(defn run []
  (d/render
   [hub]
   (js/document.getElementById "root")))





(defn ^:export init []
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))
