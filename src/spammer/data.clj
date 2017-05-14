(ns spammer.data
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; #+BEGIN_SRC clojure
;; {:email-address "john123@dired.com"
;;  :spam-score 0.12}
;; #+END_SRC

(def sample-email {:email-address "john123@dired.com" :spam-score 0.12})

(def email-domains
  #{"indeediot.com"
    "monstrous.com"
    "linkedarkpattern.com"
    "dired.com"
    "lice.com"
    "careershiller.com"
    "glassbore.com"})

(def email-regex
  #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def ::email-address
  (s/with-gen
    (s/and string? #(re-matches email-regex %))
    #(->>
      (gen/tuple (gen/such-that not-empty (gen/string-alphanumeric))
                 (s/gen email-domains))
      (gen/fmap (fn [[addr domain]] (str addr "@" domain))))))

(s/def ::spam-score
  (s/double-in :min 0 :max 1))

(s/def ::email-record
  (s/keys :req-un [::email-address ::spam-score]))


;; --------------------------------------------------------------------------------
;; Development generate function
;; Add this to project.clj
;;   :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}

(defn generate-email-record []
  (gen/generate (s/gen ::email-record)))

(defn generate 
  ([] (repeatedly generate-email-record))
  ([num] (take num (generate))))


;; For building examples with 0.1 for a spam-score
(defn generate-fixed-score [num spam-score]
  (take num (repeatedly  (fn [] {:email-address (gen/generate (s/gen ::email-address)) :spam-score spam-score}))))
