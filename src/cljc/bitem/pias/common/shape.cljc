(ns bitem.pias.common.shape
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def :general/section
  #{:chart :account :template})

(s/def :client/name string?)

(s/def :style/key
  #{:button
    :is-primary
    :is-success
    :navbar    
    :navbar-brand
    :navbar-item
    :navbar-start
    :navbar-end
    :navigation})
