(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as rdom]))

(defnc app []
  (d/div
   (d/h1 {:class "code"}
         "Hello World!")
   (d/button {:class "btn btn-success"}
             "Click SuccessButton!")
   (d/button {:class "btn btn-secondary"}
             "Click SecondaryButton!")))

(defn render []
  (rdom/render
   ($ app)
   (js/document.getElementById "root")))

(defn ^:export init []
  (render))
