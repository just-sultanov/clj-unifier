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

(deftest handler-test
  (let [res1 (handler "john@doe.com")
        res2 (handler "jane@doe.com")
        res3 (handler "andrew@doe.com")]
    (is (= {:status 200, :body user1} res1))
    (is (= {:status 200, :body user2} res2))
    (is (= {:status 404, :body "user not found"} res3))))


(deftest response-test
  (let [res1 (get-user-by-email "john@doe.com")
        res2 (get-user-by-email "jane@doe.com")
        res3 (get-user-by-email "andrew@doe.com")]
    (is (true? (sut/response? res1)))
    (is (true? (sut/success? res1)))
    (is (false? (sut/error? res1)))
    (is (= :ok (sut/get-type res1)))
    (is (= user1 (sut/get-data res1)))
    (is (nil? (sut/get-meta res1)))

    (is (true? (sut/response? res2)))
    (is (true? (sut/success? res2)))
    (is (false? (sut/error? res2)))
    (is (= :ok (sut/get-type res2)))
    (is (= user2 (sut/get-data res2)))
    (is (nil? (sut/get-meta res2)))

    (is (true? (sut/response? res3)))
    (is (false? (sut/success? res3)))
    (is (true? (sut/error? res3)))
    (is (= :not-found (sut/get-type res3)))
    (is (= "user not found" (sut/get-data res3)))
    (is (nil? (sut/get-meta res3)))))
