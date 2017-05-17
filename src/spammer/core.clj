(ns spammer.core
  (:gen-class)
  (:require [spammer.process :as process]
            [spammer.data :as data]))

(defn -main [& args]
  ;; TODO read filename or number of emails to test with

  
  (let [cnt (if args (Integer. (first args)) 10)
        data (data/generate cnt)
        send-emails (process/process-input data)]
    (println "Processing " (count data) " sample email data records")
    (println "--------------------------------------------------------------------------------")
    (println "Send " (count send-emails))
    (doseq [r (take 10 send-emails)]
      (println (:email-address r))
      )
    (if (> (count send-emails) 10)
      (println "...")
      )
    )
  )
