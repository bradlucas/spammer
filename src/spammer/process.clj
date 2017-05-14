(ns spammer.process
  (:require [spammer.data :as data]))

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

;; #+BEGIN_SRC clojure
;; {:email-address "john123@dired.com"
;;  :spam-score 0.12}
;; #+END_SRC

(def max-spam-score 0.3)
(def max-running-mean 0.05)
(def max-mean-recent-100 0.1)


(defn sent-add
  "Add a new record to the sent set and return the new set"
  [email-record sent-emails]
  (if (empty? sent-emails)
    (conj '() email-record)
    (conj sent-emails email-record)))

(defn sent-contains?
  "Return true if the sent-emails set contains the email-record

NOTE:
This version is inefficient because we are using a list for sent-emails and to 
check if it contains a specific email we map the emails into a set and then
use the set to see if the email was in the original list.

"
  [email-record sent-emails]
  ((set (map :email-address sent-emails)) (:email-address email-record)))

(defn new-email?
  "Return true if the email is not in the sent-emails set"
  [email-record sent-emails]
  (nil? (sent-contains? email-record sent-emails)))

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

(defn running-mean 
  "Return the mean of the list of email-records"
  [email-records]
  (calc-spam-score-mean email-records))

(defn recent-mean 
  "Return the mean of the most recent 100 email records"
  [email-records]
  (calc-spam-score-mean email-records 100))

(defn ok-to-send 
  "Check if email-record is good to send"
  [email-record sent-emails]
  (let [running-mean (running-mean (conj sent-emails email-record))
        recent-mean (recent-mean (conj sent-emails email-record))]
    (and (new-email? email-record sent-emails)
         (valid-spam-score email-record)
         (valid-running-mean running-mean)
         (valid-recent-mean  recent-mean))))

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
      (recur (rest records) 
             (let [email-record (first records)]
               (if (ok-to-send email-record sent-emails)
                 (sent-add email-record sent-emails)
                 sent-emails))))))


