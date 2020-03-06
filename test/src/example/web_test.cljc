(ns example.web-test
  (:require
   [example.helpers :as helpers]))

(defn req []
  {:api/version        :v1
   :session/id         (helpers/new-id)
   :request/id         (helpers/new-id)
   :request/csrf-token (helpers/new-id)
   :i18n/language      :en
   :cmd/name           :user/get
   :cmd/version        :v1
   :cmd/context        {:user/email "john@doe.com"}})
