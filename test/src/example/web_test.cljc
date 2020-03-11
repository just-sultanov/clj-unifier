(ns example.web-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [unifier.response :as r]
   [example.helpers :as helpers]
   [example.data :as data]
   [example.web :as sut]))

;;;;
;; Test helpers
;;;

(defn- with-req
  ([cmd]
   (with-req :en cmd))

  ([language cmd]
   (with-req :v1 language cmd))

  ([version language cmd]
   (merge cmd
     {:request/id         (helpers/new-id)
      :request/csrf-token (helpers/new-id)
      :session/id         (helpers/new-id)
      :api/version        version
      :i18n/language      language})))


(defn- with-cmd
  ([name]
   (with-cmd name nil))

  ([name context]
   (with-cmd name :v1 context))

  ([name version context]
   {:cmd/name    name
    :cmd/version version
    :cmd/context context}))


(defn same? [status type x]
  (and
    (= status (:status x))
    (= type (get-in x [:body :type]))
    true))



;;;;
;; Tests
;;;;

(deftest web-test
  (is (same? 405 ::r/unsupported
        (sut/cmd-handler (with-req (with-cmd ::unknown)))))

  (is (same? 405 ::r/unsupported
        (sut/cmd-handler (with-req :v2 :en (with-cmd :user/get-all)))))

  (is (same? 200 :users/found
        (sut/cmd-handler (with-req (with-cmd :users/get-all)))))

  (is (same? 200 :user/found
        (sut/cmd-handler (with-req (with-cmd :user/get data/user1)))))

  (is (same? 200 :user/found
        (sut/cmd-handler (with-req (with-cmd :user/get data/user2)))))

  (is (same? 409 :user/not-created
        (sut/cmd-handler (with-req (with-cmd :user/create data/user1)))))

  (is (same? 409 :user/not-created
        (sut/cmd-handler (with-req (with-cmd :user/create data/user2)))))

  (is (same? 404 :user/not-found
        (sut/cmd-handler (with-req (with-cmd :user/get {:user/email "andrew@doe.com"})))))

  (is (same? 201 :user/created
        (sut/cmd-handler (with-req (with-cmd :user/create {:user/email "andrew@doe.com"})))))

  (is (same? 200 :user/found
        (sut/cmd-handler (with-req (with-cmd :user/get {:user/email "andrew@doe.com"})))))

  (is (same? 204 :user/deleted
        (sut/cmd-handler (with-req (with-cmd :user/delete {:user/email "andrew@doe.com"}))))))
