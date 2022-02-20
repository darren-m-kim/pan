(ns bitem.pias.client.element.management
  (:require
   [clojure.spec.alpha :as s]
   [reagent.core :as r]
   [bitem.pias.common.shape :as h]))

(def loc-name (r/atom nil))

(def loc-num-persons (r/atom nil))

(defn loc-clientele []
  {:post [(s/valid? ::h/clientele %)]}
  (let [m {:name @loc-name
           :num-persons @loc-num-persons}]
    (print "your generate local client is " m)
    m))

(defn form []
  [:div
   [:label "Management Name: "]
   [:input {:type "text"
            :value @loc-name
            :on-change #(reset! loc-name
                                (-> % .-target .-value))}]
   [:br]
   [:label "Number of End Users: "]
   [:input {:type "number"
            :value @loc-num-persons
            :on-change #(reset! loc-num-persons
                                (-> % .-target .-value
                                    js/parseInt))}]
   [:br] [:br]
   [:button {:type "Submit"
             :key "a"
             :on-click (fn [] (print "hoho" (loc-clientele)))}
    "submit"]])
