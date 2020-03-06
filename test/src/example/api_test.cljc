(ns example.api-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [unifier.response :as r]
   [example.data :as data]
   [example.api :as sut]))

(defn- with-cmd
  ([name]
   (with-cmd name nil))

  ([name context]
   (with-cmd name :v1 context))

  ([name version context]
   {:cmd/name    name
    :cmd/version version
    :cmd/context context}))


(deftest api-test
  (is (r/error? (sut/execute (with-cmd ::unknown))))
  (is (= 2 (count (r/get-data (sut/execute (with-cmd :users/get-all))))))
  (is (r/success? (sut/execute (with-cmd :user/get data/user1))))
  (is (r/success? (sut/execute (with-cmd :user/get data/user2))))
  (is (r/error? (sut/execute (with-cmd :user/create data/user1))))
  (is (r/error? (sut/execute (with-cmd :user/create data/user2))))
  (is (r/error? (sut/execute (with-cmd :user/get {:user/email "andrew@doe.com"}))))
  (is (r/success? (sut/execute (with-cmd :user/create {:user/email "andrew@doe.com"}))))
  (is (= 3 (count (r/get-data (sut/execute (with-cmd :users/get-all))))))
  (is (r/success? (sut/execute (with-cmd :user/delete {:user/email "andrew@doe.com"}))))
  (is (r/error? (sut/execute (with-cmd :user/get {:user/email "andrew@doe.com"}))))
  (is (= 2 (count (r/get-data (sut/execute (with-cmd :users/get-all)))))))
