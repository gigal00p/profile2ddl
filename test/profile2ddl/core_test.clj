(ns profile2ddl.core-test
  (:require [clojure.test :refer :all]
            [profile2ddl.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))


(deftest test-int-or-bigint
  (let [min -2147483648
        max 2147483647]
    (is (= (int-or-bigint min max) " INTEGER"))))


(deftest test-normalize-column-name
  (let [multi-word-column " DNA SEQUENCE NUMER"]
    (is (= (normalize-column-name multi-word-column) "dnasequencenumer"))))
