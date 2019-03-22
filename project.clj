(defproject mvxcvi/edn-tool "0.2.0-SNAPSHOT"
  :description "Command-line EDN printing tool."
  :url "https://github.com/greglook/edn-tool"
  :license {:name "Public Domain"
            :url "http://unlicense.org/"}

  :pedantic? :abort

  :dependencies
  [[org.clojure/clojure "1.10.0"]
   [org.clojure/tools.cli "0.4.1"]
   [mvxcvi/puget "1.1.1"]]

  :main mvxcvi.edn.main

  :profiles
  {:uberjar
   {:target-path "target/uberjar"
    :uberjar-name "edn.jar"
    :aot :all}})
