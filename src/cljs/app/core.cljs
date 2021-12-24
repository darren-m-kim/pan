(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as rdom]))

(defnc app []
  (d/div
   (d/h1 "Hello World!")))

(defn render []
  (rdom/render
   ($ app)
   (js/document.getElementById "root")))

(defn ^:export init []
  (render))
