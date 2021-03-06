(ns example.api
  (:require
   [unifier.response :as r]
   [example.user.service :as user.service]))

;; This is a tiny API layer
;; NOTE: E.g. you can check user permissions before executing any commands

(defmulti execute
  (fn [{:cmd/keys [version name]}]
    [version name]))


(defmethod execute :default
  [{:as cmd :cmd/keys [version name]}]
  (r/as-unsupported cmd {:i18n/key ::unsupported :i18n/params [version name]}))


(defmethod execute [:v1 :users/get-all]
  [_]
  (user.service/get-all))


(defmethod execute [:v1 :user/get]
  [{:cmd/keys [context]}]
  (user.service/get (:user/email context)))


(defmethod execute [:v1 :user/create]
  [{:cmd/keys [context]}]
  (user.service/create (:user/email context)))


(defmethod execute [:v1 :user/delete]
  [{:cmd/keys [context]}]
  (user.service/delete (:user/email context)))
