(ns profile2ddl.helper-test
  (:require [clojure.test :refer :all]
            [profile2ddl.helper :refer :all]))

(deftest test-normalize-column-name
  (let [multi-word-column " DNA SEQUENCE NUMER"]
    (is (= (normalize-column-name multi-word-column) "dnasequencenumer"))))


(deftest test-int-or-bigint
  (let [min -2147483648
        max 2147483647]
    (is (= (int-or-bigint min max) " INTEGER"))))
