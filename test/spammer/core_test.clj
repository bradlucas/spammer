(ns spammer.core-test
  (:require [clojure.test :refer :all]
            [amalloy.ring-buffer :refer [ring-buffer]]
            [spammer.process :as process]
            [spammer.data :as data]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

;; (deftest b-test
;;   (testing "I succeed"
;;     (is true)))

(deftest valid-spam-score
  (testing "valid-spam-core"
    (is (process/valid-spam-score {:email-address "john123@dired.com" :spam-score process/max-spam-score}))))


(deftest valid-running-mean
  (testing "valid-running-mean"
    (is (process/valid-running-mean process/max-running-mean))))

(deftest valid-recent-mean
  (testing "valid-recent-mean"
    (is (process/valid-recent-mean process/max-mean-recent-100))))

(deftest mean 
  (testing "mean"
    (is (= 101/2 (/ (apply + (range 1 101)) 100)))))

(deftest calc-spam-score-mean
  (testing "calc-spam-score-mean"
    (is (= 0.5 (process/calc-spam-score-mean [{:spam-score 0.4} {:spam-score 0.6}])))))

(deftest new-running-mean 
  (testing "new-running-mean"
    (let [acc {:sent-emails nil
               :running-total 10
               :running-count 4
               :recent-100 nil}
          email-record {:email-address "foo@foo.com" :spam-score 5}]
      (println acc)
      (println email-record)
      (let [val (process/new-running-mean acc email-record)]
        (println val)
        (is (= 3 val))
        ))))

(deftest recent-mean 
  (testing "TODO - build list of 100+ known records and then test validating the first 100's mean"
    (is (= 0.5 (process/recent-mean (into (ring-buffer 100) (map :spam-score (data/generate-fixed-score 200 0.5))))))))

(deftest new-email-true
  "New mail is one that is not in the set of sent-emils which is a list of email-records"
  (testing "new-mail"
    (is (process/new-email? #{"test@test.com"} {:email-address "foo@foo.com" :spam-score 0.2}))))

(deftest new-email-false
  "New mail is one that is not in the set of sent-emils which is a list of email-records"
  (testing "new-mail"
    (is (not (process/new-email? #{"foo@foo.com"}  {:email-address "foo@foo.com" :spam-score 0.1})))))


;; 1. Must send a maximum of one email per address.
;; 2. Must never send an email with a spam score of more than 0.3.
;; 3. The running mean of spam ratings must never get above 0.05. (In
;;    other words, as each email is processed the mean spam score of all
;;    the emails that have sent in the batch needs to be recalculated and
;;    can't ever be more than 0.05.)
;; 4. The mean spam score of the most recent 100 emails sent can't go
;;    above 0.1.

;; TODO these need more work...
(deftest ok-to-send-1
  "Test if ok-to-send fails an email that has already been sent"
  (testing "1. Must send a maximum of one email per address."
    (let [acc {:sent-emails #{"foo1@foo.com"}
               :running-total 0    ;; total of all :spam-code values for sent-emails
               :running-count 0    ;; number of sent-emails
               :recent-100    nil  ;; ring-buffer with recent 100 sent-emails
               }
          ]
      (is (not (process/ok-to-send acc {:email-address "foo1@foo.com" :spam-score 0.001})))
      )
    ))

(deftest ok-to-send-1-1
  "Test if ok-to-send allows a new email"
  (testing "1. Must send a maximum of one email per address."
    (let [acc {:sent-emails #{"foo1@foo.com"}
               :running-total 0    ;; total of all :spam-code values for sent-emails
               :running-count 0    ;; number of sent-emails
               :recent-100    nil  ;; ring-buffer with recent 100 sent-emails
               }
          ]
      (is (process/ok-to-send acc {:email-address "foo2@foo.com" :spam-score 0.001}))
      )
    ))

(deftest ok-to-send-2
  "Test check for email's spam score being less than or equal to 0.3"
  (testing "2. Must never send an email with a spam score of more than 0.3."
    (let [acc {:sent-emails #{"foo1@foo.com"}
               :running-total 0    ;; total of all :spam-code values for sent-emails
               :running-count 0    ;; number of sent-emails
               :recent-100    nil  ;; ring-buffer with recent 100 sent-emails
               }
          ]
      (is (process/ok-to-send acc {:email-address "foo2@foo.com" :spam-score 0.001}))
      )
    ))

(deftest ok-to-send-3
  "Test if ok to send when running mean is less than 0.05"
  (testing "3. The running mean of spam ratings must never get above 0.05."
    (let [acc {:sent-emails #{"foo1@foo.com"}
               :running-total 0.002   ;; total of all :spam-code values for sent-emails
               :running-count 2    ;; number of sent-emails
               :recent-100    nil  ;; ring-buffer with recent 100 sent-emails
               }
          ]
      (is (process/ok-to-send acc {:email-address "foo3@foo.com" :spam-score 0.001}))
      )
))

;; TODO Fix this when recent-100 is implemented
;; (deftest ok-to-send-4
;;   "Test send if recent 100 mean less then 0.1"
;;   (testing ""
;;     (let [acc {:sent-emails '({:email-address "foo1@foo.com" :spam-score 0.001})
;;                :running-total 0    ;; total of all :spam-code values for sent-emails
;;                :running-count 0    ;; number of sent-emails
;;                :recent-100    nil  ;; ring-buffer with recent 100 sent-emails
;;                }
;;           ]
;;       (is (process/ok-to-send acc {:email-address "foo2@foo.com" :spam-score 0.001}))
;;       )
;;     ))

;; TODO but ok-to-send is the meat of things
;; (deftest process-input 
;;   (testing ""
;;     (is true)))

