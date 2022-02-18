(ns bitem.pias.common.shape
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as t]))

;;;; instrumentation ;;;;

(t/instrument)
#_(t/unstrument)


;;;; individual ;;;;

(s/def ::name string?)

(s/def ::entity-id uuid?)

(s/def ::user-hash uuid?)


;;;; subject ;;;;

(def subject
  [:client :person :chart
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


;;;; content ;;;;

(def content
  [:client-sign-up :person-sign-up
   :person-sign-in :dashboard
   :entity :chart
   :account :template
   :transact :ledger
   :journal :report])

(def content?
  (set (concat content
               (map name content))))

(s/def ::content
  (into #{} content))


;;;; client ;;;;

(s/def ::num-persons
  (s/and int? pos? #(< % 500)))

(s/def ::client
  (s/keys :req-un [::name ::num-persons ::subject]
          :opt-un [::entity-id ::user-hash]))


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

(s/def ::clients
  (s/coll-of uuid?
             :kind vector?
             :distinct true))

(s/def ::person
  (s/keys :req-un [::subject ::name ::abilities]
          :opt-un [::clients ::entity-id]))
