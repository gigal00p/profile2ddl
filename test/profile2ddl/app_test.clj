(ns profile2ddl.app-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [profile2ddl.helper :as hp]
            [profile2ddl.app :as ap :refer :all]))


(deftest test-emit-ddl-string
  (let [input {:min "1",
               :mean "2.949857921294019",
               :stddev "3.241907447432876",
               :field "EESTATU",
               :type "Integer",
               :max_length "1",
               :max "9",
               :min_length "1",
               :sum "314226677"}
        result [\space
                \space
                \space
                \space
                \e
                \e
                \s
                \t
                \a
                \t
                \u
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \space
                \I
                \N
                \T
                \E
                \G
                \E
                \R
                \,
                \newline]]
    (is (= (ap/emit-ddl-string true input) result))))


(deftest test-process-one-file
  (let [test-file-name "/tmp/profile2ddl/tests/process-one-file/test-csv-profile.csv"
        test-data "field,type,sum,min,max,min_length,max_length,mean,stddev\nSEQNUM NUMBER,Integer,5677245305943586,44,106596505,2,9,53296133.85074341,30778094.264473855\nVERSION,Unicode,,E1,E1,2,2,,"
        expected-results "CREATE TABLE test-csv-profile (\n    seqnumnumber         INTEGER,\n    version              VARCHAR(2)\n);\n"
        results-file-name "/tmp/profile2ddl/tests/process-one-file/test-csv-profile.ddl.sql"]
    (do
      (io/make-parents test-file-name)
      (io/make-parents results-file-name)
      (spit test-file-name test-data)
      (ap/process-one-file test-file-name "/tmp/profile2ddl/tests/process-one-file"))
    (is (= (slurp results-file-name) expected-results)))
  (hp/delete-recursively "/tmp/profile2ddl"))
