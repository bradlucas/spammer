(ns spammer.process
  (:require [amalloy.ring-buffer :refer [ring-buffer]] ;; @see https://github.com/amalloy/ring-buffer
            [spammer.data :as data]))

(use 'clojure.pprint)
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
  [acc email-record]
    (assoc acc 
      :sent-emails (conj (:sent-emails acc) (:email-address email-record))
      :running-total (+ (:running-total acc) (:spam-score email-record))
      :running-count (inc (:running-count acc))
      :recent-100 (into (:recent-100 acc) (list (:spam-score email-record)))
      ))

(defn sent-contains?
  "Return true if the sent-emails set contains the email-record

NOTE:
This version replaces the previous list based one with an ordered-set.
With this we can use contains to check for the email-record.
"
  [sent-emails email-record]
  (sent-emails (:email-address email-record)))

(defn new-email?
  "Return true if the email is not in the sent-emails set"
  [sent-emails email-record]
  (nil? (sent-contains? sent-emails email-record)))

(defn valid-spam-score [email-record]
  (<= (:spam-score email-record) max-spam-score))

(defn valid-running-mean [val]
  (<= val max-running-mean))

(defn valid-recent-mean [val]
  (if (nil? val)
    0
    (<= val max-mean-recent-100)))

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

(defn new-running-mean 
  "Return the new mean after consider a new email-record"
  [acc email-record]
  (/ (+ (:running-total acc) (:spam-score email-record)) (inc (:running-count acc))))

(defn recent-mean 
  "Return the mean of the most recent 100 email record's spam-score values"
  [vals]
  (mean vals))

(defn ok-to-send 
  "Check if email-record is good to send"
  [acc email-record]
  ;; (clojure.pprint/pprint acc)
  (let [sent-emails (:sent-emails acc)
        running-mean (new-running-mean acc email-record)
        recent-mean (recent-mean (:recent-100 acc))]
    (and (new-email? sent-emails email-record)
         (valid-spam-score email-record)
         (valid-running-mean running-mean)
         (valid-recent-mean recent-mean))))

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
         acc {:sent-emails #{}    ;; set
              :running-total 0    ;; total of all :spam-code values for sent-emails
              :running-count 0    ;; number of sent-emails
              :recent-100 (ring-buffer 100)     ;; ring-buffer with recent 100 sent-emails
              }]
    (if (empty? records)
      (:sent-emails acc)
      (recur (rest records) 
             (let [email-record (first records)]
               (if (ok-to-send acc email-record)
                 (sent-add acc email-record)
                 acc))))))


