(ns app.core
  (:require [app.parser :as p]))

(defn run [opts]
  (prn (p/combine "Hi, " "LOONGA!")))
