(ns unifier.response-test
  (:require
   #?(:clj  [clojure.test :refer [deftest testing is use-fixtures]]
      :cljs [cljs.test :refer-macros [deftest testing is use-fixtures]])
   [unifier.response.http :as http]
   [unifier.response :as sut]))

;;;;
;; Fixtures
;;;

(defonce registry @sut/*registry)

(defn reset-changes! []
  (reset! sut/*registry registry))


(use-fixtures :each
  (fn [f]
    (reset-changes!)
    (f)
    (reset-changes!)))



;;;;
;; Test helpers
;;;;

(defn same? [type data meta x]
  (and
    (= type (sut/get-type x))
    (= data (sut/get-data x))
    (= meta (sut/get-meta x))
    true))

(defn same-http? [status headers body x]
  (and
    (= status (:status x))
    (= headers (:headers x))
    (= body (:body x))
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
      (is (same? ::sut/busy data meta (sut/as-busy data)))))


  (testing "2-arity"
    (let [data "response"
          meta "meta"]
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



(deftest link-user-defined-types-test
  (testing "link one pair"
    (is (nil? (get @sut/*registry ::ok1)))
    (is (nil? (get @sut/*registry ::bad-request1)))
    (is (nil? (get @sut/*registry ::not-found1)))
    (is (true? (sut/link! ::ok1 ::http/ok)))
    (is (true? (sut/link! ::bad-request1 ::http/bad-request)))
    (is (true? (sut/link! ::not-found1 ::http/not-found)))
    (is (= ::http/ok (get @sut/*registry ::ok1)))
    (is (= ::http/bad-request (get @sut/*registry ::bad-request1)))
    (is (= ::http/not-found (get @sut/*registry ::not-found1))))


  (testing "link multiple pairs"
    (is (nil? (get @sut/*registry ::ok2)))
    (is (nil? (get @sut/*registry ::bad-request2)))
    (is (nil? (get @sut/*registry ::not-found2)))
    (is (true? (sut/link!
                 ::ok2 ::http/ok
                 ::bad-request2 ::http/bad-request
                 ::not-found2 ::http/not-found)))
    (is (= ::http/ok (get @sut/*registry ::ok2)))
    (is (= ::http/bad-request (get @sut/*registry ::bad-request2)))
    (is (= ::http/not-found (get @sut/*registry ::not-found2))))


  (testing "link existing pairs"
    (is (= ::http/ok (get @sut/*registry ::ok1)))
    (is (= ::http/bad-request (get @sut/*registry ::bad-request1)))
    (is (= ::http/not-found (get @sut/*registry ::not-found1)))
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::ok1 ::http/ok)))
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::bad-request1 ::http/bad-request)))
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::not-found1 ::http/not-found)))
    (is (true? (sut/link ::ok1 ::http/ok)))
    (is (true? (sut/link ::bad-request1 ::http/bad-request)))
    (is (true? (sut/link ::not-found1 ::http/not-found)))
    (is (true? (sut/link
                 ::ok1 ::http/ok
                 ::bad-request1 ::http/bad-request
                 ::not-found1 ::http/not-found)))
    (is (= ::http/ok (get @sut/*registry ::ok1)))
    (is (= ::http/bad-request (get @sut/*registry ::bad-request1)))
    (is (= ::http/not-found (get @sut/*registry ::not-found1))))


  (testing "link bad http response types"
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::ok ::ok)))
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::bad-request ::bad-request)))
    (is (thrown? #?(:clj IllegalArgumentException, :cljs js/Error)
          (sut/link! ::not-found ::not-found1)))))



(deftest as-http-test
  (let [data      "data"
        meta      "meta"
        with-type (fn [type] {:type type :data data :meta meta})]

    (testing "common `success` unified responses"
      (is (same-http? 200 {} (with-type ::sut/success)
            (sut/as-http (sut/as-success data meta))))

      (is (same-http? 201 {} (with-type ::sut/created)
            (sut/as-http (sut/as-created data meta))))

      (is (same-http? 204 {} (with-type ::sut/deleted)
            (sut/as-http (sut/as-deleted data meta))))

      (is (same-http? 202 {} (with-type ::sut/accepted)
            (sut/as-http (sut/as-accepted data meta)))))


    (testing "common `error` unified responses"
      (is (same-http? 500 {} (with-type ::sut/error)
            (sut/as-http (sut/as-error data meta))))

      (is (same-http? 500 {} (with-type ::sut/exception)
            (sut/as-http (sut/as-exception data meta))))

      (is (same-http? 400 {} (with-type ::sut/unknown)
            (sut/as-http (sut/as-unknown data meta))))

      (is (same-http? 400 {} (with-type ::sut/warning)
            (sut/as-http (sut/as-warning data meta))))

      (is (same-http? 503 {} (with-type ::sut/unavailable)
            (sut/as-http (sut/as-unavailable data meta))))

      (is (same-http? 400 {} (with-type ::sut/interrupted)
            (sut/as-http (sut/as-interrupted data meta))))

      (is (same-http? 400 {} (with-type ::sut/incorrect)
            (sut/as-http (sut/as-incorrect data meta))))

      (is (same-http? 401 {} (with-type ::sut/unauthorized)
            (sut/as-http (sut/as-unauthorized data meta))))

      (is (same-http? 403 {} (with-type ::sut/forbidden)
            (sut/as-http (sut/as-forbidden data meta))))

      (is (same-http? 405 {} (with-type ::sut/unsupported)
            (sut/as-http (sut/as-unsupported data meta))))

      (is (same-http? 404 {} (with-type ::sut/not-found)
            (sut/as-http (sut/as-not-found data meta))))

      (is (same-http? 409 {} (with-type ::sut/conflict)
            (sut/as-http (sut/as-conflict data meta))))

      (is (same-http? 503 {} (with-type ::sut/busy)
            (sut/as-http (sut/as-busy data meta)))))


    (testing "`user-defined` unified responses"
      (is (same-http? 200 {} (with-type ::done)
            (sut/as-http (sut/as-success ::done data meta))))

      (sut/link! ::created ::http/created)
      (is (same-http? 201 {} (with-type ::created)
            (sut/as-http (sut/as-success ::created data meta))))

      (is (same-http? 400 {} (with-type ::fail)
            (sut/as-http (sut/as-error ::fail data meta))))

      (sut/link! ::fail ::http/internal-server-error)
      (is (same-http? 500 {} (with-type ::fail)
            (sut/as-http (sut/as-error ::fail data meta)))))))
