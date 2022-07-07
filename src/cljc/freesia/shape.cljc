(ns freesia.shape
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as t]))

;;;; instrumentation ;;;;

(t/instrument)
#_(t/unstrument)


;;;; general individual ;;;;

(s/def ::name string?)

(s/def ::entity-id uuid?)

(s/def ::rid (s/and int? pos?))

(s/def ::user-hash uuid?)

(def password?
  #(re-matches #"((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,24})" %))

(s/def ::password password?)


;;;; subject ;;;;

(def subject
  [:management :person :chart
   :account :transact :journal])

(def subject?
  (set (concat subject
               (map name subject))))

(s/def ::subject subject?)


;;;; email ;;;

(defn email? [s]
  (let [pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (and (string? s) (re-matches pattern s))))

(s/def ::email email?)


;;;; tag ;;;;

(def status
  [:drafted :posted])

(def status?
  (into #{} status))

(s/def ::created-at inst?)

(s/def ::valid-from inst?)

(s/def ::valid-till inst?)

(s/def ::status status?)

(s/def ::tag
  (s/keys :req-un [::created-at ::status]
          :opt-un [::valid-from ::valid-till]))


;;;; db act ;;;;

(def db-act [:read
             :insert
             :update
             :delete])

(def db-act? (set db-act))

(s/def ::db-act db-act?)


;;;; element ;;;;
;; for client ;;;

(def element
  [:management-register
   :management-list
   :person-register
   :person-sign
   #_ (comment :dashboard :entity
            :chart :account
            :template :transact
            :ledger :journal
            :report)])

(def element?
  (set (concat element
               (map name element))))

(s/def ::element
  (into #{} element))


;;;; management ;;;;

(s/def ::num-persons
  (s/and int? pos? #(< % 500)))

(s/def ::management-req
  (s/keys :req-un [::name ::num-persons ::subject]
          :opt-un [::entity-id ::user-hash]))

(s/def ::management
  (s/keys :req-un [::name ::num-persons ::subject
                   ::entity-id ::user-hash]))


;;;; person ;;;; 

(def ability
  [:read
   :write
   :edit
   :delete
   :admin])

(def ability?
  (set (concat ability
               (map name ability))))

(s/def ::abilities
  (s/coll-of ability?
             :kind vector?
             :distinct true))

(s/def ::managements
  (s/coll-of uuid?
             :kind vector?
             :distinct true))

(s/def ::person-req
  (s/keys :req-un [::subject ::name ::abilities
                   ::email ::password]
          :opt-un [::managements ::entity-id]))

(s/def ::person
  (s/keys :req-un [::subject ::name ::abilities
                   ::email ::password
                   ::managements ::entity-id]))

(s/def ::sign-in-req
  (s/keys :req-un [::email ::password]))
