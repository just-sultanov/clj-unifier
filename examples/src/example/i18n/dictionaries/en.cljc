(ns example.i18n.dictionaries.en)

(def dictionary
  {:users/received  (fn [x]
                      (cond
                        (zero? x) "The user list is empty"
                        (= 1 x) "Received a list of 1 user"
                        :else "Received a list of {1} user"))
   :user/received   "User `{1}` successfully received"
   :user/exists     "User with email `{1}` is exists"
   :user/not-exists "User with email `{1}` isn't exists"
   :user/created    "User with email `{1}` successfully created"
   :user/deleted    "User with email `{1}` successfully deleted"})