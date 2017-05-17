(defproject spammer "1.0.1"
  :description "Programming assignment to filter emails by spam-score following a set of assigned rules"
  :url "https://github.com/bradlucas/spammer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojure "1.9.0-alpha16"]
                 [amalloy/ring-buffer "1.2.1"]
                 [org.clojure/test.check "0.9.0"]]
  ;; :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :main spammer.core
)
