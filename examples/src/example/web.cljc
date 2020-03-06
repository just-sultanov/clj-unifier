(ns example.web
  (:require
   [unifier.response :as r]
   [unifier.response.http :as http]
   [example.i18n :as i18n]
   [example.api :as api]))

;;;;
;; API router
;;;;

(defmulti invoke
  (fn [{:api/keys [version method]}]
    [version method]))


(defmethod invoke :default [_]
  {:status 501
   :body   "not implemented"}) ;; TODO: change to unifier.response.http


(defmethod invoke [:v1 :cmd/execute]
  [req]
  (api/execute req))



;;;;
;; Mapper for the response types
;;;;

;; TODO: use derive?
(def response-types
  {:user/found       ::http/ok
   :user/not-found   ::http/not-found
   :users/found      ::http/ok
   :user/created     ::http/created
   :user/not-created ::http/conflict
   :user/deleted     ::http/no-content
   :user/not-deleted ::http/not-found})



;;;;
;; HTTP transformers
;;;;

(defn- translate-transformer [language]
  (fn [res]
    (if-some [meta (r/get-meta res)]
      (if-some [i18n-key (:i18n/key meta)]
        (let [i18n-params (some-> (:i18n/params meta) vector flatten)
              translator  (i18n/get-translator language)
              message     (apply translator i18n-key i18n-params)
              meta        (-> meta (assoc :i18n/message message) (dissoc :i18n/key :i18n/params))]
          (r/set-meta res meta))
        res)
      res)))



(defn- http-transformer [res]
  (let [type          (r/get-type res)
        response-type (get response-types type)]
    {:status (http/to-status response-type)
     :body   res}))


(defn- transform [opts]
  (let [tfs (:transformers opts)]
    (fn [res]
      (reduce
        (fn [acc tf]
          (tf acc))
        res tfs))))



;;;;
;; HTTP handlers
;;;;

(defn cmd-handler [req]
  (let [language (:i18n/language req)
        opts     {:transformers [(translate-transformer language)
                                 http-transformer]}
        req      (assoc req :api/method :cmd/execute)
        tf       (transform opts)]
    (tf (invoke req))))



;;;;
;; HTTP router
;;;

;; NOTE: In the router write your own invoker by API version, custom parameters, and your requirements
;; In this example, we have only 2 routes, which uses only one internal API
;; 1. /ws         - websocket handler
;; 2. /api/v1/cmd - accepts only POST requests

;; TBD
