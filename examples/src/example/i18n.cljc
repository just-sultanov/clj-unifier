(ns example.i18n
  (:require
   [tongue.core :as tongue]
   [example.i18n.dictionaries.en :as en]
   [example.i18n.dictionaries.ru :as ru]))

(def dictionary
  {:en              en/dictionary
   :ru              ru/dictionary
   :tongue/fallback :en})


(def translator
  (tongue/build-translate dictionary))


(defn get-translator [language]
  (let [f (partial translator language)]
    (fn [key & args]
      (apply f key args))))
