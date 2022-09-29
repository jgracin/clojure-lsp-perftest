(ns perf.core-test
  (:require [perf.core :as c]
            [clojure.test :refer [deftest testing is]]))

(deftest find-definition-in-large-project
  (testing "succesfully finding a definition"
    (is (= "file:///b.clj" (-> (c/prepare-large-project)
                        (c/find-definition)
                        :uri)))))