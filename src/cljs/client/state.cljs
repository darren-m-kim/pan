(ns client.state
  (:require [reagent.core :as r]))

(defonce dark (r/atom true))
(defonce client (r/atom {:legalname "Bitem, LLC"
                     :description "Software Firm"}))
(defonce worker (r/atom nil))
(defonce page (r/atom nil))
(defonce account (r/atom nil))

(defn show-db []
  (merge {:dark @dark}
         {:page @page}
         {:client @client}
         {:worker @worker}))
