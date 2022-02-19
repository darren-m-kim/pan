(ns bitem.pias.server.content.person
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.logging :as g]
   [ring.util.response :as i]
   [compojure.core :as p]
   [bitem.pias.server.db :as b]
   [bitem.pias.common.shape :as h]
   [bitem.pias.server.random :as r]
   [bitem.pias.server.sign :as n]))

(s/fdef client-exists?
  :args (s/cat :eid ::h/entity-id)
  :ret boolean?)
(defn client-exists? [eid]
  (let [client-ids (->> (b/read-all :client)
                        (map (comp :entity-id :doc))
                        (set))]
    (client-ids (r/uuid->str eid))))

(defn read-all-persons [_]
  (let [read (b/read-all :person)]
    (if read
      (i/response (b/read-all :person))
      (i/not-found {:status :fail}))))

(defn insert-person! [req]
  (let [body (:body req)
        clients (into [] (map r/str->uuid (:clients body)))
        new (assoc body
                   :entity-id (r/uuid)
                   :clients clients)
        clients? (every? identity (map client-exists? clients))
        conforming? (s/valid? ::h/person new)]
    (if (and conforming? clients?)
      (do (g/info "good, given body conforms!")
          (b/insert! (b/tag :insert) new)
          (i/response {:result :success}))
      (i/bad-request {:result :fail
                      :reason (cond (false? conforming?)
                                    (with-out-str (s/explain ::h/person new))
                                    (false? clients?) "some clients do not exist.")}))))

(s/fdef sign-in-persom
  :args (s/cat :email ::h/email
               :password ::h/password))
(defn sign-in [req]
  (let [{:keys [email password]} (-> req :body)
        sql (str "select * from data where "
                 "doc->>'subject' = 'person' and "
                 "doc->>'email' = '" email "' and "
                 "doc->>'password' = '" password "';")
        persons (b/run-sql sql)]
    (cond (empty? persons) (i/bad-request {:result :fail
                                           :reason "can't find the combination"})
          (< 1 (count persons)) (i/bad-request {:result :fail
                                                :reason "duplicate data detected"})
          :else (i/response (n/jwt (first persons))))))

(def person-handlers
  [(p/GET "/api/sign-in" [] sign-in)
   (p/GET "/api/person" [] read-all-persons)
   (p/POST "/api/person" [] insert-person!)])
