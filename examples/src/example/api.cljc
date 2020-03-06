(ns example.api
  (:require
   [unifier.response :as r]
   [example.user.service :as user.service]))

(defmulti execute
  (fn [{:cmd/keys [version name]}]
    [version name]))


(defmethod execute :default
  [{:as cmd :cmd/keys [name]}]
  (r/as-unsupported cmd {:i18n/key ::unsupported :i18n/params name}))


(defmethod execute [:v1 :users/get-all]
  [_]
  (user.service/get-users))


(defmethod execute [:v1 :user/get]
  [{:cmd/keys [context]}]
  (user.service/get-user (:user/email context)))


(defmethod execute [:v1 :user/create]
  [{:cmd/keys [context]}]
  (user.service/create-user (:user/email context)))


(defmethod execute [:v1 :user/delete]
  [{:cmd/keys [context]}]
  (user.service/delete-user (:user/email context)))
