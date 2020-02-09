(ns unifier.response)

;;;;
;; Defaults
;;;;

(defonce ^{:added "0.0.3" :doc "Default error `type`."}
  default-error-type
  (atom :error))

(defonce ^{:added "0.0.3" :doc "Default success `type`."}
  default-success-type
  (atom :success))


(defn set-default-error-type!
  "Overrides default error `type`."
  {:added "0.0.3"}
  [type]
  (reset! default-error-type type))

(defn set-default-success-type!
  "Overrides default success `type`."
  {:added "0.0.3"}
  [type]
  (reset! default-success-type type))



;;;;
;; Response protocols
;;;;

(defprotocol IUnifiedResponse
  (-response? [_]
    "Returns `true` if the given value is implements `unifier.response/IUnifiedResponse` protocol. Otherwise `false`.")
  (-error? [_]
    "Returns `true` if the given value is instance of `unifier.response/UnifiedError`. Otherwise `false`.")
  (-success? [_]
    "Returns `true` if the given value is instance of `unifier.response/UnifiedSuccess`. Otherwise `false`.")
  (-get-type [_]
    "Returns `type` of response.")
  (-get-data [_]
    "Returns `data` of response.")
  (-get-meta [_]
    "Returns `meta` of response."))

;; Extends all objects for compatibility.
;; Returns `false` for all `IUnifiedResponse` protocol predicates and identity for `get-data`
(extend-protocol IUnifiedResponse
  #?(:clj Object :cljs js/Object)
  (-response? [_] false)
  (-error? [_] false)
  (-success? [_] false)
  (-get-type [_] nil)
  (-get-data [_] _)
  (-get-meta [_] nil))



;;;;
;; Getters wrappers
;;;;

(defn response?
  "Returns `true` if the given value is implements `unifier.response/IUnifiedResponse` protocol. Otherwise `false`."
  {:added "0.0.3"}
  [x]
  (-response? x))

(defn error?
  "Returns `true` if the given value is instance of `unifier.response/UnifiedError`. Otherwise `false`."
  {:added "0.0.3"}
  [x]
  (-error? x))

(defn success?
  "Returns `true` if the given value is instance of `unifier.response/UnifiedSuccess`. Otherwise `false`."
  {:added "0.0.3"}
  [x]
  (-success? x))

(defn get-type
  "Returns `type` of response."
  {:added "0.0.3"}
  [x]
  (-get-type x))

(defn get-data
  "Returns `data` of response."
  {:added "0.0.3"}
  [x]
  (-get-data x))

(defn get-meta
  "Returns `meta` of response."
  {:added "0.0.3"}
  [x]
  (-get-meta x))



;;;;
;; Response types
;;;;

(defrecord UnifiedError [type data meta]
  IUnifiedResponse
  (-response? [_] true)
  (-error? [_] true)
  (-success? [_] false)
  (-get-type [_] type)
  (-get-data [_] data)
  (-get-meta [_] meta))

(defrecord UnifiedSuccess [type data meta]
  IUnifiedResponse
  (-response? [_] true)
  (-error? [_] false)
  (-success? [_] true)
  (-get-type [_] type)
  (-get-data [_] data)
  (-get-meta [_] meta))



;;;;
;; Response builders
;;;;

(defn as-error
  "Returns instance of `unifier.response/UnifiedError`.
  Examples:
    * with default error `type`
    (as-error \"a user was not found\")

    * with specified error `type`
    (as-error :user/not-found \"a user was not found\")

    * with specified error `type` and `meta`
    (as-error :user/not-found \"john@doe.com\" {:i18/key :user/not-found})"
  {:added "0.0.3"}
  ([data]
   (as-error @default-error-type data nil))
  ([type data]
   (as-error type data nil))
  ([type data meta]
   (->UnifiedError type data meta)))

(defn as-success
  "Returns instance of `unifier.response/UnifiedSuccess`.
  Examples:
    * with default success `type`
    (as-success \"a user was created successfully\")

    * with specified success `type`
    (as-success :user/created \"a user was created successfully\")

    * with specified success `type` and `meta`
    (as-success :user/created \"a user was created successfully\" {:i18/key :user/user/created})"
  {:added "0.0.3"}
  ([data]
   (as-success @default-success-type data nil))
  ([type data]
   (as-success type data nil))
  ([type data meta]
   (->UnifiedSuccess type data meta)))



;;;;
;; Response handlers
;;;;

(defmulti as-response
  "A dispatcher for the unified responses."
  get-type)

;; Returns identity by default
(defmethod as-response :default [x] x)
(defmethod as-response nil [x] x)
