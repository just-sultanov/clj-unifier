(ns unifier.response.http-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [unifier.response.http :as sut]))

(deftest to-status-test
  (is (= 100 (sut/to-status ::sut/continue)))
  (is (= 200 (sut/to-status ::sut/ok)))
  (is (= 300 (sut/to-status ::sut/multiple-choices)))
  (is (= 400 (sut/to-status ::sut/bad-request)))
  (is (= 500 (sut/to-status ::sut/internal-server-error)))
  (is (= ::sut/unknown (sut/to-status ::unknown))))


(deftest to-type-test
  (is (= ::sut/unknown (sut/to-type 0)))
  (is (= ::sut/continue (sut/to-type 100)))
  (is (= ::sut/ok (sut/to-type 200)))
  (is (= ::sut/multiple-choices (sut/to-type 300)))
  (is (= ::sut/bad-request (sut/to-type 400)))
  (is (= ::sut/internal-server-error (sut/to-type 500)))
  (is (= ::sut/unknown (sut/to-type 600))))
