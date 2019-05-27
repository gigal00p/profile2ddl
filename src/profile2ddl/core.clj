(ns profile2ddl.core
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre :refer [info errorf]]
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


(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (hp/exit 0 (help summary))
      (not= (count options) 2) (hp/exit 0 (help summary))
      :else
      (try
        (let [input-dir (->> options :input)
              output-dir (->> options :output)
              files (hp/files-to-process input-dir)]
          (do (if (= (count files) 0)
                (hp/exit 0 (timbre/info "No files to process found, exiting")))
              (if (hp/check-path-exist? input-dir)
                (timbre/info "Input directory is" input-dir)
                (hp/exit 1 "Dir does not exist, exiting."))
              (if (hp/check-path-exist? output-dir)
                (timbre/info "Output directory is" output-dir)
                (hp/exit 1 "Dir does not exist, exiting."))
              (timbre/info "Files to process:" (count files)))
          (doall (map #(ap/process-one-file % output-dir) files)))
        (catch Exception e
          (timbre/errorf "Something when wrong: %s" (.getMessage ^Exception e))
          (System/exit 1))))))
