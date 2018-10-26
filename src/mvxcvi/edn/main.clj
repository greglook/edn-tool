(ns mvxcvi.edn.main
  (:gen-class)
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [puget.printer :as puget])
  (:import
    (java.io
      PushbackReader
      Writer)))


; TODO: way to customize puget options?
(def cli-options
  [["-i" "--input FILE" "Read input from the given file instead of STDIN"]
   ["-o" "--output FILE" "Write output to the given file instead of STDOUT"]
   [nil  "--no-color" "Don't output in color"]
   ["-w" "--width SIZE" "Column at which to wrap print output"
    :default 200
    :parse-fn #(Integer/parseInt %)]
   ["-h" "--help" "Show help"]])


(defn- read-print-loop
  "Read EDN from the input reader until the end of the stream is reached,
  printing it to the output writer. Closes the output stream at the end of the
  loop."
  [input printer output]
  (let [sentinel (Object.)]
    (try
      (binding [*out* output]
        (loop []
          (let [value (edn/read {:eof sentinel
                                 :default tagged-literal}
                                input)]
            (when (not= sentinel value)
              (puget/render-out printer value)
              (flush)
              (recur)))))
      (finally
        (when (not= output *out*)
          (.close ^java.io.Writer output))))))


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
    (let [printer (puget/pretty-printer
                    {:width (:width options)
                     :print-color (not (:no-color options))
                     :sort-keys 100})
          input (if-let [path (:input options)]
                  (PushbackReader. (io/reader (io/file path)))
                  *in*)
          output (if-let [path (:output options)]
                   (io/writer (io/file path))
                   *out*)]
      (read-print-loop input printer output))))
