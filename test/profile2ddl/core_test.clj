(ns profile2ddl.core-test
  (:require [clojure.test :refer :all]
            [profile2ddl.core :refer :all]))

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
    (is (= (emit-ddl-string true input) result))))
 
