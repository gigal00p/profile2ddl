(ns profile2ddl.core
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre :refer [info error errorf]]
            [clojure.tools.cli :refer [parse-opts]]
            [eftest.runner :refer [find-tests run-tests]]
            [profile2ddl.app :as ap]
            [profile2ddl.helper :as hp]))

(def cli-options
  [["-i" "--input DIR" "Directory with profiles csv files produced by xsv tool"]
   ["-o" "--output DIR" "Directory where DDL sql files will be written"]
   ["-h" "--help"]])


(defn help [options]
  (->> ["profile2ddl is a command line tool for converting output of `xsv stats` into sql ddl files."
        ""
        "Usage: java -jar profile2ddl-0.1.0-SNAPSHOT-standalone.jar [options]"
        ""
        "Options:"
        options
        ""]
       (str/join \newline)))


(defn validate-path
  [path-type path]
  (if (hp/check-path-exist? path)
    (do (timbre/info "Passed" path-type "directory is" path) path)
    (do (error path-type "is not a directory or does not exists:" (str "`"path"`"))
        (hp/exit 1 "No valid path specified, exiting..."))))


(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (hp/exit 0 (help summary))
      (not= (count options) 2) (hp/exit 0 (str "Not enough options provided, usage:\n\n" (help summary)))
      (not= (count errors) 0) (hp/exit 0 (str "CLI arguments parsing failed, usage:\n\n" (help summary)))
      :else
      (try
        (let [input-dir (->> options :input (validate-path "input"))
              output-dir (->> options :output (validate-path "output"))
              files (hp/files-to-process input-dir)]

          (if (= (count files) 0) (hp/exit 0 (timbre/info "No files to process found, exiting")))

          (timbre/info "Files to process:" (count files))

          (doall (map #(ap/process-one-file % output-dir) files)))

        (catch Exception e
          (timbre/errorf "Something went wrong: %s" (.getMessage ^Exception e))
          (System/exit 1))))))
