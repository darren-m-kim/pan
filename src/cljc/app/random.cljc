(ns app.random)

(defn make-uuid []
  (.toString (java.util.UUID/randomUUID)))
