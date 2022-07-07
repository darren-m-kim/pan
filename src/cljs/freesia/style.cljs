(ns bitem.pias.client.style
  (:require
   [garden.core :as g]))

(defn font []
  (str "@import "
       "url('"
       "https://fonts.googleapis.com/css2?family=Source+Code+Pro&display=swap"
       "');"))

(defn body []
  (g/css [:body {:background-color :black
                 :color :white
	         :font-family "'Source Code Pro'"
                 :font-size "13px"
                      }]))

(comment 
  "
	 table, th, td {
	     border-collapse: collapse;
	     border: 1px solid;
	     text-align: center; }
	 input {
	     border: 1;
	     outline: 1;
	     background: gray;
	     border-bottom: 1px solid white; }
	 input, select, textarea{
	     color: white;
	 }")

(defn combined []
  [:style
   (str (font)
        (body))])
