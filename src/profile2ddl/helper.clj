(ns profile2ddl.helper
  (:gen-class)
  (:require [taoensso.timbre :as timbre :refer [debug  info  warn  error  fatal]]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [semantic-csv.core :as sc]
            [clojure.spec.alpha :as s]
            [eftest.runner :refer [find-tests run-tests]]
            [expound.alpha :as expound]
            [orchestra.spec.test :as st]
            [clojure.reflect :as r]
            [clojure-csv.core :as csv]
            [profile2ddl.helper-schemas :as hs]))


(defn parse-string-to-number
  "Reads a number from a string. Returns nil if not a number."
  [^String s]
  (cond (re-find #"^-?\d+\.?\d*$" s) (read-string s)
        :else nil))


(defn is-numeric-string?
  [^String s]
  (if (= (number? (parse-string-to-number s)) true)
    true
    false))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn get-full-path-files-in-dir
  "Returns absolute path of files in the passed directory.
   :recursively? keyword controles weather walk will be done recursively"
  [path & {:keys [recursively]}]
  (let [file (io/file path)]
    (if (.isDirectory file)
      (do
        (if (= true recursively)
          (file-seq file)
          (->> file
               .listFiles)))
      (do (error (str "Passed path: `" path "` is not a directory"))
          (exit -1 "Cannot proceed")))))


;; TODO refactor both functions
(defn check-path-exist?
  [fs-path]
  (try
    (let [path (io/file fs-path)]
      (if (.isDirectory path)
        true
        false))))


(s/fdef check-path-exist?
  :args (s/cat :fs-path (not empty?))
  :ret boolean?)


(defn get-table-name-from-file
  [file-path]
  (let [file-name (->> (str/split file-path #"/")
                       last)]
    (first (str/split file-name #"\."))))


(defn csv->map
  [path-to-csv-profile]
  (with-open [in-file (io/reader path-to-csv-profile)]
    (->>
     (csv/parse-csv in-file)
     (sc/remove-comments)
     (sc/mappify)
     doall)))


(defn all-methods [obj]
  (->> obj clojure.reflect/reflect
       :members 
       (filter :return-type)  
       (map :name) 
       sort 
       (map #(str "." %) )
       distinct))


(defn- is-reserved-db-word?
  [word]
  (s/valid? ::hs/redshift-rezerved-words (.toUpperCase word)))


(s/fdef is-reserved-db-word?
  :args (s/cat :word string?)
  :ret boolean?)


(defn normalize-column-name
  [s]
  (.toLowerCase (str/replace s #"\s+" "")))


(defn valid-column-name?
  [s]
  (if (is-reserved-db-word? s)
    (do (warn "Field" [s] "is database reserved keyword. DDL statement will probably fail")
        false)
    true))


(defn int-or-bigint
  "Check for Redshift integer min, max values. Returns BIGINT if crossed."
  [min max]
  (if (or (> max 2147483647) (< min -2147483648))
    " BIGINT"
    " INTEGER"))


(defn persist-file
  [path data]
  (info "Writing result file" path)
  (with-open [wrtr (io/writer path)]
    (.write wrtr data)))


(defn files-to-process
  "Returns paths to csv profiles files produced by xsv table tool."
  [dir]
  (->> (get-full-path-files-in-dir dir)
       (map #(.getAbsolutePath %))
       (filter #(str/ends-with? % ".csv"))))

(defn delete-recursively [fname]
  (doseq [f (reverse (file-seq (clojure.java.io/file fname)))]
    (clojure.java.io/delete-file f)))
