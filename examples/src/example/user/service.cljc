(ns example.user.service
  (:refer-clojure :exclude [get])
  (:require
   [unifier.response :as r]
   [example.user.repository :as repo]))

;; This is business logic layer

(defn get-all []
  (let [users (repo/get-all)]
    (r/as-success :users/found users {:i18n/key :users/received :i18n/params (count users)})))


(defn get [email]
  (if-some [user (repo/get email)]
    (r/as-success :user/found user {:i18n/key :user/received :i18n/params email})
    (r/as-error :user/not-found email {:i18n/key :user/not-exists :i18n/params email})))


(defn create [email]
  (if-some [user (repo/create email)]
    (r/as-success :user/created user {:i18n/key :user/created :i18n/params email})
    (r/as-error :user/not-created email {:i18n/key :user/exists :i18n/params email})))


(defn delete [email]
  (if-some [user (repo/delete email)]
    (r/as-success :user/deleted user {:i18n/key :user/deleted :i18n/params email})
    (r/as-error :user/not-deleted email {:i18n/key :user/not-exists :i18n/params email})))
