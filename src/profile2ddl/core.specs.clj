(ns profile2ddl.core.specs
  (:gen-class)
  (:require [clojure.spec.alpha :as s]))

(s/def ::path string?)
(s/def ::xsv-type #{:unicode :float :integer})

