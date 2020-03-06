(ns unifier.response-test
  (:require
   #?(:clj  [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]])
   [unifier.response :as sut]))

;;;;
;; Test helpers
;;;;

(defn same? [type data meta x]
  (and
    (= type (sut/get-type x))
    (= data (sut/get-data x))
    (= meta (sut/get-meta x))
    true))

(defn calc [x]
  (if-not (sut/response? x)
    (sut/as-success (inc x))
    (update x :data inc)))

(defn boom! [x]
  (sut/as-error ::sut/error "boom!" (sut/get-data x)))

(defn throw! [& _]
  #?(:clj  (throw (IllegalArgumentException. "boom!"))
     :cljs (throw (js/Error. "boom!"))))



;;;;
;; Tests
;;;;

(deftest objects-test
  (let [objects [nil true false 1 \1 "1" 'symbol :keyword ::keyword
                 #?(:clj (Object.) :cljs (js/Object.))]]
    (doseq [o objects]
      (is (false? (sut/response? o)))
      (is (false? (sut/error? o)))
      (is (false? (sut/success? o)))
      (is (nil? (sut/get-type o)))
      (is (= o (sut/get-data o)))
      (is (nil? (sut/get-meta o))))))


(deftest responses-test
  (testing "1-arity"
    (let [data "response"
          meta nil]
      (is (same? ::sut/ok data meta (sut/as-ok data)))
      (is (same? ::sut/success data meta (sut/as-success data)))
      (is (same? ::sut/created data meta (sut/as-created data)))
      (is (same? ::sut/deleted data meta (sut/as-deleted data)))
      (is (same? ::sut/accepted data meta (sut/as-accepted data)))

      (is (same? ::sut/error data meta (sut/as-error data)))
      (is (same? ::sut/exception data meta (sut/as-exception data)))
      (is (same? ::sut/warning data meta (sut/as-warning data)))
      (is (same? ::sut/unknown data meta (sut/as-unknown data)))
      (is (same? ::sut/unavailable data meta (sut/as-unavailable data)))
      (is (same? ::sut/interrupted data meta (sut/as-interrupted data)))
      (is (same? ::sut/incorrect data meta (sut/as-incorrect data)))
      (is (same? ::sut/unauthorized data meta (sut/as-unauthorized data)))
      (is (same? ::sut/forbidden data meta (sut/as-forbidden data)))
      (is (same? ::sut/unsupported data meta (sut/as-unsupported data)))
      (is (same? ::sut/not-found data meta (sut/as-not-found data)))
      (is (same? ::sut/conflict data meta (sut/as-conflict data)))
      (is (same? ::sut/fault data meta (sut/as-fault data)))
      (is (same? ::sut/busy data meta (sut/as-busy data)))))

  (testing "2-arity"
    (let [data "response"
          meta "meta"]
      (is (same? ::sut/ok data meta (sut/as-ok data meta)))
      (is (same? ::sut/success data meta (sut/as-success data meta)))
      (is (same? ::sut/created data meta (sut/as-created data meta)))
      (is (same? ::sut/deleted data meta (sut/as-deleted data meta)))
      (is (same? ::sut/accepted data meta (sut/as-accepted data meta)))

      (is (same? ::sut/error data meta (sut/as-error data meta)))
      (is (same? ::sut/exception data meta (sut/as-exception data meta)))
      (is (same? ::sut/warning data meta (sut/as-warning data meta)))
      (is (same? ::sut/unknown data meta (sut/as-unknown data meta)))
      (is (same? ::sut/unavailable data meta (sut/as-unavailable data meta)))
      (is (same? ::sut/interrupted data meta (sut/as-interrupted data meta)))
      (is (same? ::sut/incorrect data meta (sut/as-incorrect data meta)))
      (is (same? ::sut/unauthorized data meta (sut/as-unauthorized data meta)))
      (is (same? ::sut/forbidden data meta (sut/as-forbidden data meta)))
      (is (same? ::sut/unsupported data meta (sut/as-unsupported data meta)))
      (is (same? ::sut/not-found data meta (sut/as-not-found data meta)))
      (is (same? ::sut/conflict data meta (sut/as-conflict data meta)))
      (is (same? ::sut/fault data meta (sut/as-fault data meta)))
      (is (same? ::sut/busy data meta (sut/as-busy data meta)))))

  (testing "setters/getters"
    (let [from-type ::sut/ok
          to-type   ::sut/success
          from-data "from"
          to-data   "to"
          from-meta nil
          to-meta   {}
          res       (sut/as-success from-type from-data from-meta)
          unwrapped (sut/unwrap res)]
      (is (same? from-type from-data from-meta res))
      (is (same? to-type from-data from-meta (sut/set-type res to-type)))
      (is (same? to-type to-data from-meta (-> res (sut/set-type to-type) (sut/set-data to-data))))
      (is (same? to-type to-data to-meta (-> res (sut/set-type to-type) (sut/set-data to-data) (sut/set-meta to-meta))))
      (is (and (map? unwrapped) (not (sut/error? unwrapped)) (not (sut/success? unwrapped))))
      (is (= {} (sut/unwrap {}))))))



(deftest safe-test
  #?(:clj
     (do
       (is (nil? (sut/safe (throw (IllegalArgumentException. "boom!")))))
       (is (sut/error? (sut/safe (throw (IllegalArgumentException. "boom!")) sut/as-error))))

     :cljs
     (do
       (is (nil? (sut/safe (throw (js/Error. "boom!")))))
       (is (sut/error? (sut/safe (throw (js/Error. "boom!")) sut/as-error))))))


(deftest thread-first-macro-test
  (let [res1 (sut/-> 42 boom!)
        res2 (sut/-> 42 calc calc)
        res3 (sut/-> 42 calc boom! calc)
        res4 (sut/-> 42 calc calc boom! calc)
        res5 (sut/-> 42 calc calc calc calc boom!)]
    (is (same? ::sut/error "boom!" 42 res1))
    (is (same? ::sut/success 44 nil res2))
    (is (same? ::sut/error "boom!" 43 res3))
    (is (same? ::sut/error "boom!" 44 res4))
    (is (same? ::sut/error "boom!" 46 res5))))


(deftest safe-thread-first-macro-test
  (let [res1 (sut/?-> 42 calc calc boom! calc)
        res2 (sut/?-> 42 calc throw! calc boom! calc)
        res3 (sut/?-> 42 calc calc calc)]
    (is (= ::sut/error (sut/get-type res1)))
    (is (= ::sut/exception (sut/get-type res2)))
    (is (= ::sut/success (sut/get-type res3)))))


(deftest thread-last-macro-test
  (let [res1 (sut/->> 42 boom!)
        res2 (sut/->> 42 calc calc)
        res3 (sut/->> 42 calc boom! calc)
        res4 (sut/->> 42 calc calc boom! calc)
        res5 (sut/->> 42 calc calc calc calc boom!)]
    (is (same? ::sut/error "boom!" 42 res1))
    (is (same? ::sut/success 44 nil res2))
    (is (same? ::sut/error "boom!" 43 res3))
    (is (same? ::sut/error "boom!" 44 res4))
    (is (same? ::sut/error "boom!" 46 res5))))


(deftest safe-thread-last-macro-test
  (let [res1 (sut/?->> 42 calc calc boom! calc)
        res2 (sut/?->> 42 calc throw! calc boom! calc)
        res3 (sut/?->> 42 calc calc calc)]
    (is (= ::sut/error (sut/get-type res1)))
    (is (= ::sut/exception (sut/get-type res2)))
    (is (= ::sut/success (sut/get-type res3)))))
