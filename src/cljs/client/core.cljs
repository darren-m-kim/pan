(ns client.core 
  (:require
   [clojure.spec.alpha :as spec]
   [reagent.core :as r]
   [reagent.dom :refer [render]]
   [shared.shape :as shape]))

(defn header []
  [:ul {:class :header}
   [:li [:a {:class "active"} "Home"]]
   [:li [:a "News"]]
   [:li [:a "Contact"]]
   [:li [:a "About"]]])

(defn menu []
  [:div {:class :menu} "menu"])

(defn content []
  [:div {:class :content} "content"])

(defn note []
  [:div {:class :note} "note"])

(defn footer []
  [:div {:class :footer} "footer"])

(defn hub []
  [:div {:class :grid-container}
   [header]
   [menu]
   [content]
   [note]
   [footer]])

(defn run []
  (render
   [hub]
   (js/document.getElementById "root")))

(defn ^:export init []
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))
