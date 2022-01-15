(ns shared.random)

(defn make-uuid []
  (.toString (java.util.UUID/randomUUID)))
