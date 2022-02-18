(ns bitem.pias.server.content.client
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.logging :as g]
   [ring.util.response :as i]
   [compojure.core :as p]
   [bitem.pias.server.db :as b]
   [bitem.pias.common.shape :as h]
   [bitem.pias.server.random :as r]))

(defn read-all-clients [_]
  (let [read (b/read-all :client)]
    (if read
      (i/response (b/read-all :client))
      (i/not-found {:status :fail}))))

(defn insert-client! [req]
  (let [body (:body req)
        user-hash (r/uuid)
        new (assoc body
                   :entity-id (r/uuid)
                   :user-hash user-hash)]
    (if (s/valid? ::h/client new)
      (do (g/info "good, given body conforms!")
          (b/insert! (b/tag :insert) new)
          (i/response {:result :success
                       :user-hash user-hash}))
      (i/bad-request {:result :fail
                      :reason (with-out-str (s/explain ::h/client new))}))))

(def client-handlers
  [(p/GET "/api/client" [] read-all-clients)
   (p/POST "/api/client" [] insert-client!)])
