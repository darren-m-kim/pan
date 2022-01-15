(ns server.core
  (:require
   [integrant.core :as ig]
   [server.system :as system]))

(defn run [_]
  (ig/init system/config))
