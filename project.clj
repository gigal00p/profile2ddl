(defproject profile2ddl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [prismatic/schema "1.1.6"]
                 [semantic-csv "0.2.1-alpha1"]
                 [org.xerial/sqlite-jdbc "3.27.2.1"]
                 [org.clojure/tools.cli "0.4.2"]]
  :main ^:skip-aot profile2ddl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
