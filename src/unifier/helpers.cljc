(ns unifier.helpers
  #?(:clj (:refer-clojure :exclude [format]))
  (:require
   #?@(:clj  [[clojure.core :as c]]
       :cljs [[goog.string :as gstr]
              [goog.string.format]])))

(def ^{:added "0.0.10"}
  format
  "Formats a string."
  #?(:clj  c/format
     :cljs gstr/format))
