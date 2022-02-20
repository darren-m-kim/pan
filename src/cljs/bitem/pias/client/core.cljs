(ns bitem.pias.client.core 
  (:require
   [clojure.spec.alpha :as s]
   [reagent.core :as r]
   [reagent.dom :as d]
   [bitem.pias.common.shape :as h]
   ;[bitem.pias.client.state :as t]
   [bitem.pias.client.element.management-list :as m]))

(def element (r/atom nil))

(defn switch-element [c] 
  (reset! element c)
  (print "element atom changed to " @element))

(defn db []
  (merge {:element @element}))

(defn header-button [k]
  ;;{:pre [(s/valid? ::h/element k)]}
  [:li [:button {:on-click #(switch-element k)}
        (name k)]])

(defn header []
  [:ul {:class :header}
   (map header-button h/element)])

(defn hub []
  [:div {:class :grid-container}
   [header]
   [:div (case @element
           :management-list [m/table]
           :management-register [:p "coming"]
           :person-register [:p "c"]
           :person-sign [:p "c"]
           [:p "asdf"])]])

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
