(ns bitem.pias.common.shape
  (:require
   [clojure.spec.alpha :as s]))

;;;; general ;;;;

(s/def ::name string?)
(s/def ::eid uuid?)

;;;; content ;;;;

(def content [:clientele-sign-up
               :personnel-sign-up
               :personnel-sign-in
               :dashboard
               :entity
               :chart
               :account
               :template
               :transact
               :ledger
               :journal
               :report])

(s/def ::content (into #{} content))


;;;; clientele ;;;;

(s/def ::num-persons (s/and int? pos? #(< % 500)))

(s/def ::clientele
  (s/keys :req-un [::name ::num-persons]
          :opt-un [::eid]))
