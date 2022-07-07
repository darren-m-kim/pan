(ns freesia.random
  (:import (java.util UUID)))

(defn uuid []
  (UUID/randomUUID))

(defn str->uuid [s]
  (try
    (java.util.UUID/fromString s)
    (catch Exception e (print e))))

(defn uuid->str [u]
  (.toString u))
