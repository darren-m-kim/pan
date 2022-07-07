(ns freesia.content.management
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.logging :as g]
   [ring.util.response :as i]
   [compojure.core :as p]
   [freesia.db :as b]
   [freesia.shape :as h]
   [freesia.random :as r]))

(defn read-all-managements [_]
  (let [read (b/read-all :management)]
    (if read
      (i/response (b/read-all :management))
      (i/not-found {:status :fail}))))

(defn insert-management! [req]
  (let [body (:body req)
        user-hash (r/uuid)
        new (assoc body
                   :entity-id (r/uuid)
                   :user-hash user-hash)]
    (if (s/valid? ::h/management new)
      (do (g/info "good, given body conforms!")
          (b/insert! (b/tag :insert) new)
          (i/response {:result :success
                       :user-hash user-hash}))
      (i/bad-request {:result :fail
                      :reason (with-out-str (s/explain ::h/management new))}))))

(def management-handlers
  [(p/GET "/api/management" [] read-all-managements)
   (p/POST "/api/management" [] insert-management!)])
