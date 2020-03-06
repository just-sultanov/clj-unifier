(ns example.user.service-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [unifier.response :as r]
   [example.data :as data]
   [example.user.service :as sut]))

(deftest service-test
  (is (= 2 (count (r/get-data (sut/get-all)))))
  (is (r/success? (sut/get (:user/email data/user1))))
  (is (r/success? (sut/get (:user/email data/user2))))
  (is (r/error? (sut/create (:user/email data/user1))))
  (is (r/error? (sut/create (:user/email data/user2))))
  (is (r/error? (sut/get "andrew@doe.com")))
  (is (r/success? (sut/create "andrew@doe.com")))
  (is (= 3 (count (r/get-data (sut/get-all)))))
  (is (r/success? (sut/delete "andrew@doe.com")))
  (is (r/error? (sut/delete "andrew@doe.com")))
  (is (r/error? (sut/get "andrew@doe.com")))
  (is (= 2 (count (r/get-data (sut/get-all))))))
