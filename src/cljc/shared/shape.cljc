(ns shared.shape
  (:require
   [clojure.spec.alpha :as spec]))

(spec/def :client/name string?)

(spec/def :style/key
  #{:button
    :is-primary
    :is-success
    :navbar    
    :navbar-brand
    :navbar-item
    :navbar-start
    :navbar-end
    :navigation})
