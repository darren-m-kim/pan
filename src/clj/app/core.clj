(ns app.core
  (:require
   [integrant.core :as ig]
   [app.system :as system]))

(defn run [_]
  (ig/init system/config))
