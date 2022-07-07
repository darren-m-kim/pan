(ns freesia.web-test
  (:require [clojure.test :refer [deftest testing is
                                  run-all-tests]]
            [next.jdbc :refer [get-datasource execute!]]
            [next.jdbc.sql :refer [insert!]]
            [server.core :refer [db-up db-down post-client
                                 get-clients]]
            [java-time :refer [instant instant->sql-timestamp
                      local-date sql-date plus minus]]))

(def fake-clients
  [{:eid #uuid "ac197719-1f37-4c2d-a689-23872896991b"
    :crat #inst "2022-01-20T14:27:23.541207000-00:00"
    :vfrom #inst "2022-01-20T14:27:23.541207000-00:00"
    :vthru (sql-date 5000)
    :legalname "Bitem, LLC"
    :shortname "Bitem"
    :numperson 2
    :nummanager 1}])

(def fake-persons
  [{:eid #uuid "1902c205-2bc6-40b8-943b-f5b199241316"
    :crat #inst "2022-01-20T14:27:23.541207000-00:00"
    :vfrom #inst "2022-01-20T14:27:23.541207000-00:00"
    :vthru (sql-date 5000)
    :clienteid #uuid "ac197719-1f37-4c2d-a689-23872896991b"
    :firstname "Darren"
    :lastname "Kim"
    :ismanager true}
   {:eid #uuid "98d18d0b-b965-4e96-9437-ba3a281040c4"
    :crat #inst "2022-01-20T14:27:23.541207000-00:00"
    :vfrom #inst "2022-01-20T14:27:23.541207000-00:00"
    :vthru (sql-date 5000)
    :clienteid #uuid "ac197719-1f37-4c2d-a689-23872896991b"
    :firstname "Linda"
    :lastname "Lovelace"
    :ismanager false}])

(def test-db-info
  {:dbtype "postgres"
   :dbname "pan_tests"
   :host "localhost"})

(deftest db-test
  (let [test-db-conn (get-datasource test-db-info)]
    (testing "migrate down test db"
      (is (nil? (db-down test-db-conn))))
    (testing "migrate up test db"
      (is (nil? (db-up test-db-conn))))
    (testing "insert fake-client"
      (is (= 9 (count (post-client test-db-conn "Bitem, LLC" "bitem" 2 1)))))
    (testing "get fake-client"
      (is (= 1 (count (get-clients test-db-conn)))))))

(comment 
  (run-all-tests))
