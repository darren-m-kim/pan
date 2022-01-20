(ns server.core-test
  (:require [clojure.test :refer [deftest testing is]]))

(deftest small-test
  (testing "basic thing"
    (is (= 1 1))))
