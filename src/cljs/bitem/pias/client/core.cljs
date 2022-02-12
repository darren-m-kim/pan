(ns bitem.pias.client.core 
  (:require
   [clojure.spec.alpha :as s]
   #_[reagent.core :as r]
   [reagent.dom :as d]
   [bitem.pias.common.shape :as h]))

(defn header []
  [:ul {:class :header}
   [:li [:a "clientele sign-up"]]
   [:li [:a "personnel sign-up"]]
   [:li [:a "personnel sign-in"]]
   [:li [:a "dashboard"]]
   [:li [:a "entity"]]
   [:li [:a "chart"]]
   [:li [:a  "account"]]
   [:li [:a "template"]]
   [:li [:a "transact"]]
   [:li [:a "ledger"]]
   [:li [:a "journal"]]
   [:li [:a "reports"]]])

(defn content []
  [:div {:class :content} "content"])

(defn hub []
  [:div {:class :grid-container}
   [header]
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
