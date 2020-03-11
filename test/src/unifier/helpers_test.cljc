(ns unifier.helpers-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [unifier.helpers :as sut]))

(deftest format-test
  (is (= "hello, world!" (sut/format "hello, %s!" "world")))
  (is (= "hello, 42!" (sut/format "hello, %d!" 42))))
