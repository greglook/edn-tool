(defproject mvxcvi/edn-tool "0.2.0"
  :description "Command-line EDN printing tool."
  :url "https://github.com/greglook/edn-tool"
  :license {:name "Public Domain"
            :url "http://unlicense.org/"}

  :pedantic? :warn

  :dependencies
  [[org.clojure/clojure "1.9.0"]
   [org.clojure/tools.cli "0.4.1"]
   [mvxcvi/puget "1.1.1" :exclusions [org.clojure/clojure]]]

  :main mvxcvi.edn.main

  :profiles
  {:uberjar
   {:target-path "target/uberjar"
    :uberjar-name "edn.jar"
    :aot :all}})
