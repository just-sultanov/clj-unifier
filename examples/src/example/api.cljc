(ns example.api
  (:require
   [unifier.response :as r]
   [example.user.service :as user.service]))

(defmulti execute
  (fn [{:cmd/keys [version name]}]
    [version name]))


(defmethod execute :default
  [cmd]
  (r/as-error :unknown cmd)) ;; TODO: add i18n meta data


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
