(ns unifier.response-test
  (:require
    #?(:clj  [clojure.test :refer [deftest is]]
       :cljs [cljs.test :refer-macros [deftest is]])
    [unifier.response :as sut]))

(deftest square-test
  (is (= 4 (sut/square 2))))
