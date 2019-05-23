(ns profile2ddl.helper-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [profile2ddl.helper :refer :all]))


(deftest test-parse-string-to-number
  (let [data "22"]
    (is (= (parse-string-to-number data) 22))))


(deftest test-is-numeric-string?
  (let [data "123"]
    (is (= (is-numeric-string? "23") true))))


(deftest test-get-full-path-files-in-dir
  (let [test-file "/tmp/profile2ddl-test-full-path-files-in-dir"
        del-file-if-exitst (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))
        file (spit test-file (str (java.time.LocalDateTime/now) " get-full-path-files-in-dir test data\n"))
        data (->> (get-full-path-files-in-dir "/tmp") (map #(.getAbsolutePath %)))
        result (->> (filter #(= test-file %) (doall data)) first)
        clean-tmp (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))]
    (= (is result "/tmp/profile2ddl-test-persist-file.txt"))))


(deftest test-check-path-exist?
  (let [path "/tmp"]
    (is (= (check-path-exist? "/tmp") true))))


(deftest test-get-table-name-from-file
  (let [file-path "/tmp/sample_file.sql"]
    (is (= (get-table-name-from-file file-path)))))


(deftest test-csv->map
  (let [test-file "/tmp/test-csv-map.csv"
        del-file-if-exitst (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))
        csv-file (spit "/tmp/test-csv-map.csv" "field,type,sum,min,max,min_length,max_length,mean,stddev
SEQNUM NUMBER,Integer,5677245305943586,44,106596505,2,9,53296133.85074341,30778094.264473855
VERSION,Unicode,,E1,E1,2,2,,")
        result (csv->map "/tmp/test-csv-map.csv")
        clean-tmp (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))]
    (is (= (->> result
                (map #(:min %))) '("44" "E1")))))


(deftest test-all-methods
  (let [data (io/file "/tmp")]
    (is (= (->> (all-methods data)
                (filter #(= ".toPath" %))) '(".toPath")))))


(deftest test-normalize-column-name
  (let [multi-word-column " DNA SEQUENCE NUMER"]
    (is (= (normalize-column-name multi-word-column) "dnasequencenumer"))))


(deftest test-valid-column-name?
    (is (= (valid-column-name? "JOIN") false)))


(deftest test-int-or-bigint
  (let [min -2147483648
        max 2147483647]
    (is (= (int-or-bigint min max) " INTEGER"))))


(deftest test-persist-file
  (let [test-file "/tmp/profile2ddl-test-persist-file.txt"
        del-file-if-exist (if (->> (io/as-file test-file) .exists) (io/delete-file test-file))
        data (str (java.time.LocalDateTime/now) " persist-file test data\n")
        path test-file
        action (persist-file path data)
        result (slurp path)
        clean-tmp (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))]
    (is (= result data))))


(deftest test-files-to-process
  (let [test-file "/tmp/profile2ddl-test-files-to-process.csv"
        del-file-if-exist (if (->> (io/as-file test-file) .exists) (io/delete-file test-file))
        data (spit test-file (str (java.time.LocalDateTime/now) " files-to-process,test,data\n"))
        result (->> (files-to-process "/tmp") first)
        clean-tmp (if (->>(io/as-file test-file) .exists) (io/delete-file test-file))]
    (is (= result test-file))))
