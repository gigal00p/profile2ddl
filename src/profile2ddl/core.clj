(ns profile2ddl.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [taoensso.timbre :as timbre
             :refer [log  trace  debug  info  warn  error  fatal]]
            [clojure.tools.cli :refer [parse-opts]]
            [eftest.runner :refer [find-tests run-tests]]
            [expound.alpha :as expound]
            [eftest.report :refer [report-to-file]]
            [eftest.report.junit :as ju]
            [profile2ddl.helper :as hp]))

(def cli-options
  [["-i" "--input DIR" "Directory with profiles csv files produced by xsv tool"]
   ["-o" "--output DIR" "Directory where DDL sql files will be written"]
   ["-h" "--help"]])


(defn emit-ddl-string
  "Receives boolean and map of csv field attributes.
  Returns vector with string that DDL sql script expects."
  [append-comma? m]
  (let [field-name (->> (:field m) hp/normalize-column-name)
        field-type (->> (:type m) .toLowerCase keyword)
        max-numeric (:max m)
        min-numeric (:min m)
        field-max-length (:max_length m)
        line-terminator (if append-comma? ",\n" "")
        row-ddl (cond (= field-type :unicode) (str field-name " VARCHAR" "(" field-max-length ")" line-terminator)
                      (= field-type :integer) (str field-name (hp/int-or-bigint (hp/parse-string-to-number min-numeric)
                                                                                (hp/parse-string-to-number max-numeric)) line-terminator)
                      (= field-type :float)   (str field-name " DECIMAL(38,10)" line-terminator)
                      (= field-type :null)    (str field-name " VARCHAR(1)" line-terminator)   ; NULL type will be represented in database as VARCHAR(1)
                      (= field-type :nan)     (str field-name " VARCHAR(1)" line-terminator))] ; NaN type will be represented in database as VARCHAR(1)
    ;; Perform validation if key contains any db reserved word
    (hp/valid-column-name? field-name)
    ;; Format result and return as vector
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
  [full-file-path output-dir]
  (let [table-name (hp/get-table-name-from-file full-file-path)
        lom (hp/csv->map full-file-path)
        ddl-string (process-csv-map lom table-name)
        target-file (str output-dir "/" table-name ".ddl.sql")]
    (try 
      (hp/persist-file target-file ddl-string)
      (catch Exception e (str "caught exception: " (.getMessage e))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [cli (parse-opts args cli-options)
        input-dir (->> cli :options :input)
        output-dir (->> cli :options :output)
        files (hp/files-to-process input-dir)]
    (do (if (hp/check-path-exist? input-dir) (info "Input directory is" input-dir))
        (if (hp/check-path-exist? output-dir) (info "Output directory is" output-dir))
        (info "Files to process are" (pr-str files)))
    (doall (map #(process-one-file % output-dir) files))))
