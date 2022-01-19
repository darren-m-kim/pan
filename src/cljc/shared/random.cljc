(ns shared.random)

(defn uuid []
  (.toString (java.util.UUID/randomUUID)))
