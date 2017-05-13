(ns spammer.core-test
  (:require [clojure.test :refer :all]
            [spammer.process :as process]))

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

(deftest running-mean 
  (testing "running-mean"
    (is (= 0.5 (process/running-mean [{:spam-score 0.4} {:spam-score 0.6}])))))

(deftest recent-mean 
  (testing "TODO - build list of 100+ known records and then test validating the first 100's mean"
    (is (= 0.5 (process/running-mean [{:spam-score 0.4} {:spam-score 0.6}])))))

(deftest new-email-true
  "New mail is one that is not in the set of sent-emils which is a list of email-records"
  (testing "new-mail"
    (is (process/new-email '({:email-address "test@test.com" :spam-score 0.1}) "foo@foo.com"))))


(deftest new-email-false
  "New mail is one that is not in the set of sent-emils which is a list of email-records"
  (testing "new-mail"
    (is (not (process/new-email '({:email-address "foo@foo.com" :spam-score 0.1}) "foo@foo.com")))))


;; TODO these need more work...
(deftest ok-to-send 
  (testing ""
    (is true)))

(deftest process-input 
  (testing ""
    (is true)))

