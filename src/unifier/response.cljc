(ns unifier.response)

;;;;
;; Defaults
;;;;

(defonce default-error-type
  (atom :error))

(defonce default-success-type
  (atom :success))


(defn set-default-error-type! [type]
  (reset! default-error-type type))

(defn set-default-success-type! [type]
  (reset! default-success-type type))



;;;;
;; Response protocols
;;;;

(defprotocol IUnifiedResponse
  (-response? [_])
  (-error? [_])
  (-success? [_])
  (-get-type [_])
  (-get-data [_])
  (-get-meta [_]))

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

(defn response? [x]
  (-response? x))

(defn error? [x]
  (-error? x))

(defn success? [x]
  (-success? x))

(defn get-type [x]
  (-get-type x))

(defn get-data [x]
  (-get-data x))

(defn get-meta [x]
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
  ([data]
   (as-error @default-error-type data nil))
  ([type data]
   (as-error type data nil))
  ([type data meta]
   (->UnifiedError type data meta)))

(defn as-success
  ([data]
   (as-success @default-success-type data nil))
  ([type data]
   (as-success type data nil))
  ([type data meta]
   (->UnifiedSuccess type data meta)))



;;;;
;; Response handlers
;;;;

(defmulti as-response get-type)

(defmethod as-response :default [x] x)
(defmethod as-response nil [x] x)
