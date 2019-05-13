(ns profile2ddl.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer [log  trace  debug  info  warn  error  fatal  report]]
            [clojure.tools.cli :refer [parse-opts]]
            [profile2ddl.helper :as helper]))

(def cli-options
  [["-i" "--input DIR" "Directory with profiles csv files produced by xsv tool"]
   ["-o" "--output DIR" "Directory where DDL sql files will be written"]
   ["-h" "--help"]])

(defn files-to-process
  "Returns paths to csv profiles files produced by xsv table tool."
  [dir]
  (->> (helper/get-full-path-files-in-dir dir)
       (map #(.getAbsolutePath %))
       (filter #(str/ends-with? % ".csv"))))

(defn int-or-bigint
  "Check for Redshift integer min, max values. Returns BIGINT if crossed."
  [min max]
  (if (or (> max 2147483647) (< min -2147483648))
    " BIGINT"
    " INTEGER"))

(defn emit-ddl-string
  [append-comma? m]
  (let [field-name (:field m)
        field-type (:type m)
        max-numeric (:max m)
        min-numeric (:min m)
        field-max-length (:max_length m)
        line-terminator (if append-comma? ",\n" "")
        ;; int-or-bigint-value (int-or-bigint max-numeric)
        row-ddl (cond (= field-type "Unicode") (str field-name " VARCHAR" "(" field-max-length ")" line-terminator)
                      (= field-type "Integer") (str field-name (int-or-bigint (helper/parse-string-to-number min-numeric)
                                                                              (helper/parse-string-to-number max-numeric)) line-terminator)
                      (= field-type "Float")   (str field-name " DECIMAL(38,10)" line-terminator)
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

(defn persist-file
  [path data]
  (info "Writing result file" path)
  (with-open [wrtr (io/writer path)]
    (.write wrtr data)))

(defn process-one-file
  [full-file-path output-dir]
  (let [table-name (helper/get-table-name-from-file full-file-path)
        lom (helper/csv->map full-file-path)
        ddl-string (process-csv-map lom table-name)
        target-file (str output-dir "/" table-name ".ddl.sql")]
    (try 
      (persist-file target-file ddl-string)
      (catch Exception e (str "caught exception: " (.getMessage e))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [cli (parse-opts args cli-options)
        input-dir (->> cli :options :input)
        output-dir (->> cli :options :output)
        files (files-to-process input-dir)]
    (do (if (helper/check-path-exist? input-dir) (info "Input directory is" input-dir))
        (if (helper/check-path-exist? output-dir) (info "Output directory is" output-dir))
        (info "Files to process are" (pr-str files)))
    (doall (map #(process-one-file % output-dir) files))))
