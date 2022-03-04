(ns bitem.pias.client.util)

(defn tablize [m]
  (let [heads (map key m)
        tails (map val m)]
    [:table
     [:thead
      [:tr
       (map (fn [h] [:th h]) heads)]]]
    [:tbody
     [:tr
      (map (fn [t] [:td t]) tails)]]))
