(ns user
  (:require
   [integrant.repl :as igre]
   [app.system :as system]))

(igre/set-prep! (fn [] system/config))

(def go igre/go)
(def halt igre/halt)
(def reset igre/reset)
(def reset-all igre/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
