(ns spammer.process
  (:require [spammer.data :as data]))


;; #+BEGIN_SRC clojure
;; {:email-address "john123@dired.com"
;;  :spam-score 0.12}
;; #+END_SRC

;; We need you to write a system that will process our email batches and
;; decide whether or not to send each email based on the following rules:

;; 1. Must send a maximum of one email per address.
;; 2. Must never send an email with a spam score of more than 0.3.
;; 3. The running mean of spam ratings must never get above 0.05. (In
;;    other words, as each email is processed the mean spam score of all
;;    the emails that have sent in the batch needs to be recalculated and
;;    can't ever be more than 0.05.)
;; 4. The mean spam score of the most recent 100 emails sent can't go
;;    above 0.1.


;; take input as a lazy sequence

(def max-spam-score 0.3)
(def max-running-mean 0.05)
(def max-mean-recent-100 0.1)
()
;; TODO the process-input function is using a list so it can be used as a stack
;; This helps with the recent mean function as it is easy to take the recent values
;; This doesn't work when checking for new email because here we want a set
;; So, for now we build a set each time which isn't good for a final version
(defn new-email 
  "Return true if the email is not in the sent-emails set"
  [sent-emails email]
  (nil? ((set (map :email-address sent-emails)) email)))

(defn valid-spam-score [email-record]
  (<= (:spam-score email-record) max-spam-score))

(defn valid-running-mean [val]
  (<= val max-running-mean))

(defn valid-recent-mean [val]
  (<= val max-mean-recent-100))

(defn mean 
  "Return the mean (average) of a collection of numbers"
  [coll]
  (let [cnt (count coll)]
     (if (pos? cnt)
       (let [total (apply + coll)]
         (/ total cnt)))))

(defn calc-spam-score-mean
  "Return the mean of the :spam-score for a sequence of email-records"
  ([email-records]
   (let [cnt (count email-records)]
     (if (pos? cnt)
       (mean (map :spam-score email-records))
       0)))
  ([email-records num]
   (calc-spam-score-mean (take num email-records))))


(defn process-input 
  "Take a sequence of email-records and process them occurding to the rules

We need you to write a system that will process our email batches and
decide whether or not to send each email based on the following rules:

1. Must send a maximum of one email per address.
2. Must never send an email with a spam score of more than 0.3.
3. The running mean of spam ratings must never get above 0.05. (In
   other words, as each email is processed the mean spam score of all
   the emails that have sent in the batch needs to be recalculated and
   can't ever be more than 0.05.)
4. The mean spam score of the most recent 100 emails sent can't go
   above 0.1.
"
  [email-records]
  (loop [records email-records
         sent-emails '()]

    (if (empty? records) 
      sent-emails

      (let [email-record (first records)
            running-mean (calc-spam-score-mean sent-emails)
            recent-mean (calc-spam-score-mean sent-emails 100)]
        (println running-mean)
        
        ;; ok to send
        (if (and (new-email sent-emails email-record)
                 (valid-running-mean running-mean)
                 (valid-recent-mean  recent-mean)
                 (valid-spam-score email-record))
            (recur (rest records) (conj sent-emails email-record))
          (recur (rest records) sent-emails))
        )
      )
    )
)


