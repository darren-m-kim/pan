(ns bitem.pias.common.shape
  (:require
   [clojure.spec.alpha :as s]))

;;;; general ;;;;

(def collection
  [:clientele :personnel
   :chart :account
   :transact :journal])
(s/def ::name string?)
(s/def ::entity-id uuid?)
(s/def ::collection (into #{} collection))

;;;; tag ;;;;

(def status [:drafted :posted])
(s/def ::created-at inst?)
(s/def ::valid-from inst?)
(s/def ::valid-till inst?)
(s/def ::status (into #{} status))
(s/def ::tag
  (s/keys :req-un [::created-at ::status]
          :top-un [::valid-from ::valid-till]))


;;;; db act ;;;;

(s/def ::db-act #{:read :insert :update :delete})


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

(s/def ::content
  (into #{} content))


;;;; clientele ;;;;

(s/def ::num-persons
  (s/and int? pos? #(< % 500)))

(s/def ::clientele
  (s/keys :req-un [::name ::num-persons ::collection]
          :opt-un [::entity-id]))
