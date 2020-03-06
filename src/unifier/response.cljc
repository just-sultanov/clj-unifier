(ns unifier.response
  "A Clojure(Script) library for unified responses."
  #?(:clj (:refer-clojure :exclude [-> ->>]))
  #?(:clj  (:require [clojure.core :as c])
     :cljs (:require-macros unifier.response)))

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
    "Returns `meta` of response.")
  (-set-type [_ type]
    "Sets `type` of response.")
  (-set-data [_ data]
    "Sets `data` of response.")
  (-set-meta [_ meta]
    "Sets `meta` of response."))



;; Extends `nil`, `Object` and `default` for compatibility.
;; Returns `false` for all `IUnifiedResponse` protocol predicates and identity for `get-data`

(extend-protocol IUnifiedResponse
  nil
  (-response? [_] false)
  (-error? [_] false)
  (-success? [_] false)
  (-get-type [_] nil)
  (-get-data [_] _)
  (-get-meta [_] nil)
  (-set-type [_ type] nil)
  (-set-data [_ data] _)
  (-set-meta [_ meta] nil))


#?(:clj
   (extend-protocol IUnifiedResponse
     Object
     (-response? [_] false)
     (-error? [_] false)
     (-success? [_] false)
     (-get-type [_] nil)
     (-get-data [_] _)
     (-get-meta [_] nil)
     (-set-type [_ type] nil)
     (-set-data [_ data] _)
     (-set-meta [_ meta] nil))

   :cljs
   (extend-protocol IUnifiedResponse
     default
     (-response? [_] false)
     (-error? [_] false)
     (-success? [_] false)
     (-get-type [_] nil)
     (-get-data [_] _)
     (-get-meta [_] nil)
     (-set-type [_ type] nil)
     (-set-data [_ data] _)
     (-set-meta [_ meta] nil)))



;;;;
;; Getters/Setters
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


(defn set-type
  "Sets `type` of response."
  {:added "0.0.7"}
  [x type]
  (-set-type x type))


(defn set-data
  "Sets `data` of response."
  {:added "0.0.7"}
  [x data]
  (-set-data x data))


(defn set-meta
  "Sets `meta` of response."
  {:added "0.0.7"}
  [x meta]
  (-set-meta x meta))



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
  (-get-meta [_] meta)
  (-set-type [resp type] (assoc resp :type type))
  (-set-data [resp data] (assoc resp :data data))
  (-set-meta [resp meta] (assoc resp :meta meta)))


(defrecord UnifiedSuccess [type data meta]
  IUnifiedResponse
  (-response? [_] true)
  (-error? [_] false)
  (-success? [_] true)
  (-get-type [_] type)
  (-get-data [_] data)
  (-get-meta [_] meta)
  (-set-type [resp type] (assoc resp :type type))
  (-set-data [resp data] (assoc resp :data data))
  (-set-meta [resp meta] (assoc resp :meta meta)))



;;;;
;; Success response builders
;;;;

(def ^{:added "0.0.7"}
  success-types
  "Common `success` types."
  #{::ok ::success ::created ::accepted})


(defn as-success
  "Returns unified `::success` response."
  {:added "0.0.3"}
  ([data]
   (as-success ::success data nil))

  ([data meta]
   (as-success ::success data meta))

  ([type data meta]
   (->UnifiedSuccess type data meta)))


(defn as-ok
  "Returns unified `::ok` response."
  {:added "0.0.7"}
  ([data]
   (as-ok data nil))

  ([data meta]
   (as-success ::ok data meta)))


(defn as-created
  "Returns unified `::accepted` response."
  {:added "0.0.7"}
  ([data]
   (as-created data nil))

  ([data meta]
   (as-success ::created data meta)))


(defn as-accepted
  "Returns unified `::accepted` response."
  {:added "0.0.7"}
  ([data]
   (as-accepted data nil))

  ([data meta]
   (as-success ::accepted data meta)))



;;;;
;; Error response builders
;;;;

(def ^{:added "0.0.7"}
  error-types
  "Common `error` types."
  #{::error ::exception ::unknown ::warning ::unavailable ::interrupted ::incorrect
    ::forbidden ::unsupported ::not-found ::conflict ::fault ::busy})


(defn as-error
  "Returns unified `::error` response."
  {:added "0.0.3"}
  ([data]
   (as-error ::error data nil))

  ([data meta]
   (as-error ::error data meta))

  ([type data meta]
   (->UnifiedError type data meta)))


(defn as-exception
  "Returns unified `::exception` response."
  {:added "0.0.7"}
  ([data]
   (as-exception data nil))

  ([data meta]
   (as-error ::exception data meta)))


(defn as-unknown
  "Returns unified `::unknown` response."
  {:added "0.0.7"}
  ([data]
   (as-unknown data nil))

  ([data meta]
   (as-error ::unknown data meta)))


(defn as-warning
  "Returns unified `::warning` response."
  {:added "0.0.7"}
  ([data]
   (as-warning data nil))

  ([data meta]
   (as-error ::warning data meta)))


(defn as-unavailable
  "Returns unified `::unavailable` response."
  {:added "0.0.7"}
  ([data]
   (as-unavailable data nil))

  ([data meta]
   (as-error ::unavailable data meta)))


(defn as-interrupted
  "Returns unified `::interrupted` response."
  {:added "0.0.7"}
  ([data]
   (as-interrupted data nil))

  ([data meta]
   (as-error ::interrupted data meta)))


(defn as-incorrect
  "Returns unified `::incorrect` response."
  {:added "0.0.7"}
  ([data]
   (as-incorrect data nil))

  ([data meta]
   (as-error ::incorrect data meta)))


(defn as-forbidden
  "Returns unified `::forbidden` response."
  {:added "0.0.7"}
  ([data]
   (as-forbidden data nil))

  ([data meta]
   (as-error ::forbidden data meta)))


(defn as-unsupported
  "Returns unified `::unsupported` response."
  {:added "0.0.7"}
  ([data]
   (as-unsupported data nil))

  ([data meta]
   (as-error ::unsupported data meta)))


(defn as-not-found
  "Returns unified `::not-found` response."
  {:added "0.0.7"}
  ([data]
   (as-not-found data nil))

  ([data meta]
   (as-error ::not-found data meta)))


(defn as-conflict
  "Returns unified `::conflict` response."
  {:added "0.0.7"}
  ([data]
   (as-conflict data nil))

  ([data meta]
   (as-error ::conflict data meta)))


(defn as-fault
  "Returns unified `::fault` response."
  {:added "0.0.7"}
  ([data]
   (as-fault data nil))

  ([data meta]
   (as-error ::fault data meta)))


(defn as-busy
  "Returns unified `::busy` response."
  {:added "0.0.7"}
  ([data]
   (as-busy data nil))

  ([data meta]
   (as-error ::busy data meta)))



;;;;
;; Response helpers
;;;;

(defn unwrap
  "Returns unwrapped unified response (as map)."
  {:added "0.0.7"}
  [response]
  (if-not (response? response)
    response
    (into {} response)))



;;;;
;; Macros helpers
;;;;

#?(:clj
   (defn cljs?
     "Checks &env in macro and returns `true` if that cljs env. Otherwise `false`."
     {:added "0.0.6"}
     [env]
     (boolean (:ns env))))


#?(:clj
   (defmacro safe
     "Extended version of try-catch."
     {:added "0.0.6"}
     ([body]
      `(safe ~body nil))

     ([body handler]
      `(try
         ~body
         (catch ~(if-not (cljs? &env) 'Throwable :default) error#
           (when-some [handler# ~handler]
             (handler# error#)))))))



;;;;
;; Pipeline builders
;;;;

#?(:clj
   (defmacro ->
     "This macro is the same as `clojure.core/some->`, but the check is done
     using the predicate `error?` of the `IUnifiedResponse` protocol and
     the substitution occurs as in macro `->` (the `thread-first` macro)."
     {:added "0.0.5"}
     [expr & forms]
     (let [g     (gensym)
           steps (map (fn [step]
                        `(let [g# ~g]
                           (if (error? g#) g# (c/-> g# ~step))))
                   forms)]
       `(let [~g ~expr
              ~@(interleave (repeat g) (butlast steps))]
          ~(if (empty? steps)
             g
             (last steps))))))


#?(:clj
   (defmacro ?->
     "The safe version of the macro `->` (the `thread-first` macro)."
     {:added "0.0.6"}
     [& forms]
     `(safe (-> ~@forms) as-exception)))


#?(:clj
   (defmacro ->>
     "This macro is the same as `clojure.core/some->>`, but the check is done
     using the predicate `error?` of the `IUnifiedResponse` protocol and
     the substitution occurs as in macro `->>` (the `thread-last` macro)."
     {:added "0.0.5"}
     [expr & forms]
     (let [g     (gensym)
           steps (map (fn [step]
                        `(let [g# ~g]
                           (if (error? g#) g# (c/->> g# ~step))))
                   forms)]
       `(let [~g ~expr
              ~@(interleave (repeat g) (butlast steps))]
          ~(if (empty? steps)
             g
             (last steps))))))


#?(:clj
   (defmacro ?->>
     "The safe version of the macro `->>` (the `thread-last` macro)."
     {:added "0.0.6"}
     [& forms]
     `(safe (->> ~@forms) as-exception)))
