(ns example.i18n-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is]]
      :cljs [cljs.test :refer-macros [deftest is]])
   [example.i18n :as sut]))

(deftest translations-test
  (let [f (sut/get-translator :en)]
    (is (= "The user list is empty" (f :users/received 0)))
    (is (= "Received a list of 1 user" (f :users/received 1)))
    (is (= "Received a list of 2 user" (f :users/received 2)))
    (is (= "Received a list of 100 user" (f :users/received 100)))
    (is (= "User `john@doe.com` successfully received" (f :user/received "john@doe.com")))
    (is (= "User with email `john@doe.com` is exists" (f :user/exists "john@doe.com")))
    (is (= "User with email `john@doe.com` isn't exists" (f :user/not-exists "john@doe.com")))
    (is (= "User with email `john@doe.com` successfully created" (f :user/created "john@doe.com")))
    (is (= "User with email `john@doe.com` successfully deleted" (f :user/deleted "john@doe.com"))))

  (let [f (sut/get-translator :ru)]
    (is (= "Список пользователей пуст" (f :users/received 0)))
    (is (= "Список из 1 пользователя успешно получен" (f :users/received 1)))
    (is (= "Список из 2 пользователей успешно получен" (f :users/received 2)))
    (is (= "Список из 100 пользователей успешно получен" (f :users/received 100)))
    (is (= "Пользователь `john@doe.com` успешно получен" (f :user/received "john@doe.com")))
    (is (= "Пользователь с указанным email `john@doe.com` существует" (f :user/exists "john@doe.com")))
    (is (= "Пользователь с указанным email `john@doe.com` не существует" (f :user/not-exists "john@doe.com")))
    (is (= "Пользователь с указанным email `john@doe.com` успешно создан" (f :user/created "john@doe.com")))
    (is (= "Пользователь с указанным email `john@doe.com` успешно удален" (f :user/deleted "john@doe.com")))))
