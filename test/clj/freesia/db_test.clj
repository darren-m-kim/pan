(ns bitem.pias.server.db-test
  (:require
   [clojure.test :as t]
   [bitem.pias.server.db :as d]))

(t/deftest now-test
  (t/is (inst? (d/now))))

(t/deftest jsbonb-test
  (t/is (= "'{\"a\":1}'"
           (d/jsonb {:a 1}))))
