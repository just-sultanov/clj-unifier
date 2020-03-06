(ns example.user.service
  (:require
   [unifier.response :as r]
   [example.user.repository :as repo]))

(defn get-users []
  (let [users (repo/get-users)]
    (r/as-success :users/found users {:i18n/key :users/received :i18n/params (count users)})))


(defn get-user [email]
  (if-some [user (repo/get-user email)]
    (r/as-success :user/found user {:i18n/key :user/received :i18n/params email})
    (r/as-error :user/not-found email {:i18n/key :user/not-exists :i18n/params email})))


(defn create-user [email]
  (if-some [user (repo/create-user email)]
    (r/as-success :user/created user {:i18n/key :user/created :i18n/params email})
    (r/as-error :user/not-created email {:i18n/key :user/exists :i18n/params email})))


(defn delete-user [email]
  (if-some [user (repo/delete-user email)]
    (r/as-success :user/deleted user {:i18n/key :user/deleted :i18n/params email})
    (r/as-error :user/not-deleted email {:i18n/key :user/not-exists :i18n/params email})))
