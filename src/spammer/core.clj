(ns spammer.core
  (:gen-class)
  (:require [spammer.process :as process]
            [spammer.data :as data]))

(defn -main [& args]
  ;; TODO read filename or number of emails to test with
  
  (let [cnt (if args (Integer. (first args)) 10)
        data (data/generate cnt)
        acc (process/process-input data)]
    (println "--------------------------------------------------------------------------------")
    (println "Processed " (count data) " sample email data records")
    (println "--------------------------------------------------------------------------------")
    (process/debug-print-acc acc)))
