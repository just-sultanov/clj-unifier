(ns example.data
  (:require
   [example.helpers :as helpers]))

(def user1 {:user/id    (helpers/new-id)
            :user/email "john@doe.com"
            :user/role  :admin
            :session/id (helpers/new-id)})

(def user2 {:user/id    (helpers/new-id)
            :user/email "jane@doe.com"
            :user/role  :user})
