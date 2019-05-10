(ns profile2ddl.core
  (:gen-class)
  (:require [clojure.string :as str]
            [profile2ddl.helper :as helper]))

(def directory-with-profiles "/Users/walkiewk/code/python/profile-to-ddl/resources")
(def directory-for-ddl "/Users/walkiewk/code/python/profile-to-ddl/resources/ddl")

(defn files-to-process
  [dir]
  (->> (helper/get-full-path-files-in-dir dir)
       (map #(.getAbsolutePath %))
       (filter #(str/ends-with? % ".csv"))))

(defn emit-ddl-string
  [append-comma? m]
  (let [field-name (:field m)
        field-type (:type m)
        field-max-length (:max_length m)
        line-terminator (if append-comma? ",\n" "")
        row-ddl (cond (= field-type "Unicode") (str field-name " VARCHAR" "(" field-max-length ")" line-terminator)
                      (= field-type "Integer") (str field-name " INTEGER" line-terminator)
                      (= field-type "Float")   (str field-name " DECIMAL" line-terminator)
                      (= field-type "NULL")    (str field-name " VARCHAR(1)" line-terminator)
                      (= field-type "NaN")     (str field-name " VARCHAR(1)" line-terminator))]
    (->> (str/split row-ddl #" ")
         (apply format "    %-20s %s")
         (into []))))

(defn process-csv-map
  [list-of-maps table-name]
  (let [records-map (butlast list-of-maps)
        last-entry (last list-of-maps)
        create-table-stm ["CREATE TABLE " table-name " (\n"]
        ;; need this distinction because last element do not need coma appanded
        majority-ddls (into [] (map #(emit-ddl-string true %) records-map))
        last-ddl (emit-ddl-string false last-entry)
        table-end-stm ["\n);"]]
    (->> (flatten [create-table-stm 
                   majority-ddls
                   last-ddl
                   table-end-stm])
         (str/join))))

(defn process-one-file
  [full-file-path]
  (let [table-name (helper/get-table-name-from-file full-file-path)
        lom (helper/csv->map full-file-path)
        ddl-string (process-csv-map lom table-name)]
    (spit (str directory-for-ddl "/" table-name ".ddl.sql") ddl-string)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hi there!")
  (->> (files-to-process directory-with-profiles)
     (map #(process-one-file %))))

