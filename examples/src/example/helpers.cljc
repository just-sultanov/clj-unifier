(ns example.helpers
  (:require
   [clojure.string :as str])
  #?(:clj
     (:import (java.util UUID))))

(defn new-id []
  #?(:clj  (UUID/randomUUID)
     :cljs (random-uuid)))


(defn index-by [f coll]
  (->> coll
    (reduce
      (fn [acc el]
        (assoc! acc (f el) el))
      (transient {}))
    persistent!))


(def prepare-email (comp str/lower-case str/trim))
