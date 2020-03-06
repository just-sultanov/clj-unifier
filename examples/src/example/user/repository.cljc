(ns example.user.repository
  (:require
   [example.helpers :as helpers]
   [example.data :as data]))

;; NOTE: You can also use unified responses for this layer

(defonce *db (atom (helpers/index-by :user/id [data/user1 data/user2])))


(defn get-users []
  (vec (vals @*db)))


(defn get-user [email]
  (let [q (helpers/prepare-email email)]
    (reduce-kv
      (fn [acc _ v]
        (if (= (:user/email v) q)
          (reduced v)
          acc))
      nil
      @*db)))


(defn create-user [email]
  (when-not (get-user email)
    (let [id   (helpers/new-id)
          user {:user/id id, :user/email email}]
      (swap! *db assoc id user)
      user)))


(defn delete-user [email]
  (when-some [user (get-user email)]
    (swap! *db dissoc (:user/id user))
    user))
