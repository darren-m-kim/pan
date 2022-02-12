(ns bitem.pias.client.core 
  (:require
   [clojure.spec.alpha :as s]
   [reagent.core :as r]
   [reagent.dom :as d]
   [bitem.pias.common.shape :as h]
   [bitem.pias.client.state :as t]
   [bitem.pias.client.content.clientele :as c]))

(defn header-button [k]
  ;;{:pre [(s/valid? ::h/content k)]}
  [:li [:button {:on-click
                 #(t/switch-content k)}
        (name k)]])

(defn header []
  [:ul {:class :header}
   (map header-button h/content)])

(defn content []
  [:div {:class :content}
   "content"])

(defn hub []
  [:div {:class :grid-container}
   [header]
   (case @t/content
     :clientele-sign-up [c/form]
     :personnel-sign-up [:p "b"]
     :personnel-sign-in [:p "c"]
     :dashboard [:p "d"]
     :entity [:p "e"]
     :chart [:p "f"]
     :account [:p "g"]
     :template [:p "h"]
     :transact [:p "i"]
     :ledger [:p "j"]
     :journal [:p "k"]
     :report [:p "l"]
     [:p "no content is selected!"]
     )])

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
