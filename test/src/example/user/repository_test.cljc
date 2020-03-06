(ns example.user.repository_test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [example.data :as data]
   [example.user.repository :as sut]))

(deftest repo-test
  (is (= 2 (count (sut/get-all))))
  (is (some? (sut/get (:user/email data/user1))))
  (is (some? (sut/get (:user/email data/user2))))
  (is (nil? (sut/create (:user/email data/user1))))
  (is (nil? (sut/create (:user/email data/user2))))
  (is (nil? (sut/get "andrew@doe.com")))
  (is (some? (sut/create "andrew@doe.com")))
  (is (= 3 (count (sut/get-all))))
  (is (some? (sut/delete "andrew@doe.com")))
  (is (nil? (sut/delete "andrew@doe.com")))
  (is (nil? (sut/get "andrew@doe.com")))
  (is (= 2 (count (sut/get-all)))))
