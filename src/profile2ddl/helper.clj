(ns profile2ddl.helper
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [semantic-csv.core :as sc]
            [clojure.reflect :as r]
            [clojure-csv.core :as csv]
            [taoensso.timbre :as timbre
             :refer [log  trace  debug  info  warn  error  fatal  report]]
            ))

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
      (do (error "Passed path is not a directory")
          (throw (Exception. "Passed path is not a directory"))))))


(defn check-path-exist?
  [fs-path]
  (let [path (io/file fs-path)]
    (if (.isDirectory path)
      true
      (throw (Exception. "Passed path is not directory or does not exists")))))
        
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


(defn all-methods [x]
    (->> x clojure.reflect/reflect
           :members 
           (filter :return-type)  
           (map :name) 
           sort 
           (map #(str "." %) )
           distinct))

