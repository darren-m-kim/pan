(ns bitem.pias.server.content.clientele
  (:require
   [clojure.spec.alpha :as s]
   [ring.util.response :as i]
   [compojure.core :as p]
   [bitem.pias.server.db :as b]
   [bitem.pias.common.shape :as h]))

(defn read-all-clientele [_]
  (prn "$$$" (b/read-all :clientele))
  (i/response  (b/read-all :clientele)))

(defn insert-clientele! [req]
  (let [raw (:body req)
        coll (:collection raw)
        m (assoc raw :collection (keyword coll))]
    (prn "@@@@" m)
    (if (s/valid? ::h/clientele m)
      (do (prn "NICE!")
          (b/insert! (b/tag :insert) m)
          (i/response {:result :success
                       :clientele-user-hash "abcd"}))
      (i/response {:result :fail}))))

(def clientele-handlers
  [(p/GET "/api/clientele" [] read-all-clientele)
   (p/POST "/api/clientele" [] insert-clientele!)])

