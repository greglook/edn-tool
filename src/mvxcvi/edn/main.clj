(ns mvxcvi.edn.main
  "Main tool entry namespace."
  (:gen-class)
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [puget.printer :as puget])
  (:import
    (java.io
      File
      PushbackReader
      Writer)))


(def default-config
  {:width 120
   :print-color true
   :sort-keys 100})


(def cli-options
  "Command-line option specs for the tool."
  [["-i" "--input FILE" "Read input from the given file instead of STDIN"]
   ["-o" "--output FILE" "Write output to the given file instead of STDOUT"]
   [nil  "--no-color" "Don't output in color"]
   ["-w" "--width SIZE" "Column at which to wrap print output"
    :parse-fn #(Integer/parseInt %)]
   ["-c" "--config FILE" "Path to configuration file to use"
    :default (str (or (System/getenv "XDG_CONFIG_HOME")
                      (str (System/getenv "HOME") "/.config"))
                  "/mvxcvi/edn-tool.edn")]
   ["-h" "--help" "Show help"]])


(defn- load-config
  "Read the tool configuration using the given options and return a printer
  configuration map."
  [options]
  (let [config-file (io/file (:config options))]
    (merge
      default-config
      (when (.exists config-file)
        (try
          (edn/read-string (slurp config-file))
          (catch Exception ex
            (binding [*out* *err*]
              (printf "WARNING: failed to load configuration from %s: %s\n"
                      (:config options)
                      (.getMessage ex))
              (flush))
            nil)))
      (when-let [width (:width options)]
        {:width width})
      (when (:no-color options)
        {:print-color false}))))


(defn- read-print-loop
  "Read EDN from the input reader until the end of the stream is reached,
  printing it to the output writer. Closes the output stream at the end of the
  loop."
  [input printer ^Writer output]
  (let [sentinel (Object.)]
    (try
      (binding [*out* output]
        (loop []
          (let [value (edn/read {:eof sentinel
                                 :default tagged-literal}
                                input)]
            (when-not (identical? sentinel value)
              (puget/render-out printer value)
              (flush)
              (recur)))))
      (finally
        (when-not (identical? output *out*)
          (.close output))))))


(defn -main
  "Main entry point for the EDN tool."
  [& args]
  (let [{:keys [options arguments summary errors]} (cli/parse-opts args cli-options)]
    (when errors
      (binding [*out* *err*]
        (doseq [err errors]
          (println errors)))
      (System/exit 1))
    (when (:help options)
      (println "Usage: edn [options] < input.edn > output.edn")
      (newline)
      (println summary)
      (flush)
      (System/exit 0))
    (let [printer (puget/pretty-printer (load-config options))
          input (if-let [path (:input options)]
                  (PushbackReader. (io/reader (io/file path)))
                  *in*)
          output (if-let [path (:output options)]
                   (io/writer (io/file path))
                   *out*)]
      (read-print-loop input printer output))))
