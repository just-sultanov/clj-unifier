(ns example.user.repository
  (:refer-clojure :exclude [get])
  (:require
   [example.helpers :as helpers]
   [example.data :as data]))

;; NOTE: You can also use unified responses for this layer

(defonce *db (atom (helpers/index-by :user/id [data/user1 data/user2])))


(defn get-all []
  (vec (vals @*db)))


(defn get [email]
  (let [q (helpers/prepare-email email)]
    (reduce-kv
      (fn [acc _ v]
        (if (= (:user/email v) q)
          (reduced v)
          acc))
      nil
      @*db)))


(defn create [email]
  (when-not (get email)
    (let [id   (helpers/new-id)
          user {:user/id id, :user/email email}]
      (swap! *db assoc id user)
      user)))


(defn delete [email]
  (when-some [user (get email)]
    (swap! *db dissoc (:user/id user))
    user))
