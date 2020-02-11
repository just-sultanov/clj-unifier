(ns unifier.response-test
  (:require
    #?(:clj  [clojure.test :refer [deftest is]]
       :cljs [cljs.test :refer-macros [deftest is]])
    [clojure.string :as str]
    [unifier.response :as sut]))

;;;;
;; Database layer
;;;;

(def user1 {:user/id 1, :user/email "john@doe.com"})
(def user2 {:user/id 2, :user/email "jane@doe.com"})

(defonce users [user1 user2])



;;;;
;; Business logic layer
;;;;

(def prepare-email (comp str/lower-case str/trim))

(defn get-user-by-email [email]
  (let [email (prepare-email email)]
    (if-some [user (->> users
                     (filter #(= (:user/email %) email))
                     first)]
      (sut/as-success :ok user)
      (sut/as-error :not-found "user not found"))))



;;;;
;; HTTP layer
;;;;

;; HTTP helpers

(defn with-status [status body]
  {:status status
   :body   body})

(defn with-type [type x]
  (if (vector? type)
    (update x :type #(conj type %))
    (assoc x :type type)))

(defn as-http-response [x]
  (sut/as-response (with-type [:http] x)))


;; HTTP wrappers

(defmethod sut/as-response [:http :ok] [x]
  (with-status 200 (sut/get-data x)))

(defmethod sut/as-response [:http :not-found] [x]
  (with-status 404 (sut/get-data x)))


;; HTTP handlers

(defn handler [email]
  (as-http-response (get-user-by-email email)))



;;;;
;; Test cases
;;;;

;; Test helpers

(defn reset-defaults! []
  (sut/set-default-error-type! ::sut/error)
  (sut/set-default-exception-type! ::sut/exception)
  (sut/set-default-success-type! ::sut/success))

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


;; Tests

(deftest defaults-overrides-test
  (reset-defaults!)

  (let [error ::error]
    (is (= ::sut/error (sut/get-type (sut/as-error "boom!"))))
    (sut/set-default-error-type! error)
    (is (= error (sut/get-type (sut/as-error "boom!")))))

  (let [exception ::exception]
    (is (= ::sut/exception (sut/get-type (sut/?-> (throw!)))))
    (sut/set-default-exception-type! exception)
    (is (= exception (sut/get-type (sut/?->> (throw!))))))

  (let [success ::success]
    (is (= ::sut/success (sut/get-type (sut/as-success "done!"))))
    (sut/set-default-success-type! success)
    (is (= success (sut/get-type (sut/as-success "done!")))))

  (reset-defaults!))


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


(deftest business-logic-test
  (let [res1 (get-user-by-email "john@doe.com")
        res2 (get-user-by-email "jane@doe.com")
        res3 (get-user-by-email "andrew@doe.com")]
    (is (sut/response? res1))
    (is (sut/success? res1))
    (is (not (sut/error? res1)))
    (is (same? :ok user1 nil res1))

    (is (sut/response? res2))
    (is (sut/success? res2))
    (is (not (sut/error? res2)))
    (is (same? :ok user2 nil res2))

    (is (sut/response? res3))
    (is (not (sut/success? res3)))
    (is (sut/error? res3))
    (is (same? :not-found "user not found" nil res3))))


(deftest http-layer-test
  (let [res1 (handler "john@doe.com")
        res2 (handler "jane@doe.com")
        res3 (handler "andrew@doe.com")]
    (is (= {:status 200, :body user1} res1))
    (is (= {:status 200, :body user2} res2))
    (is (= {:status 404, :body "user not found"} res3))))


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
