(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn simple-example []
  [:div
   [:h2 "abcdef"]])

(def id-to-html "root")

(defn run []
  (rdom/render [simple-example]
               (js/document.getElementById id-to-html)))

(defn ^:export init []
  (run)
  (js/console.log "Loaded"))

(defn ^:export refresh []
  (run)
  (js/console.log "Hot reload"))
