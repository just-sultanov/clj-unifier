(ns unifier.response.http
  "Unified HTTP responses."
  (:require
   [clojure.set :as set]))

;;;;
;; Defaults
;;;;

(def ^{:added "0.0.7"}
  default-success-type
  "Default unified http `success` type."
  ::ok)


(def ^{:added "0.0.7"}
  default-client-error-type
  "Default unified http `client error` type."
  ::bad-request)


(def ^{:added "0.0.7"}
  default-server-error-type
  "Default unified http `server error` type."
  ::internal-server-error)



;;;;
;; HTTP codes
;;;;

(def ^{:added "0.0.7"}
  informational-status-codes
  "HTTP `informational` 1xx status codes."
  {::continue            100
   ::switching-protocols 101
   ::processing          102
   ::early-hints         103})


(def ^{:added "0.0.7"}
  success-status-codes
  "HTTP `success` 2xx status codes."
  {::ok                            200
   ::created                       201
   ::accepted                      202
   ::non-authoritative-information 203
   ::no-content                    204
   ::reset-content                 205
   ::partial-content               206})


(def ^{:added "0.0.7"}
  redirection-status-codes
  "HTTP `redirection` 3xx status codes."
  {::multiple-choices   300
   ::moved-permanently  301
   ::found              302
   ::see-other          303
   ::not-modified       304
   ::use-proxy          305
   ::switch-proxy       306
   ::temporary-redirect 307
   ::permanent-redirect 308})


(def ^{:added "0.0.7"}
  client-error-status-codes
  "HTTP `client error` 4xx status codes."
  {::bad-request                     400
   ::unauthorized                    401
   ::payment-required                402
   ::forbidden                       403
   ::not-found                       404
   ::method-not-allowed              405
   ::not-acceptable                  406
   ::proxy-authentication-required   407
   ::request-timeout                 408
   ::conflict                        409
   ::gone                            410
   ::length-required                 411
   ::precondition-failed             412
   ::request-entity-too-large        413
   ::request-uri-too-long            414
   ::unsupported-media-type          415
   ::requested-range-not-satisfiable 416
   ::expectation-failed              417})


(def ^{:added "0.0.7"}
  server-error-status-codes
  "HTTP `server error` 5xx status codes."
  {::internal-server-error      500
   ::not-implemented            501
   ::bad-gateway                502
   ::service-unavailable        503
   ::gateway-timeout            504
   ::http-version-not-supported 505})


(def ^{:added "0.0.7"}
  response-type->http-status
  "HTTP status codes."
  (merge informational-status-codes
    success-status-codes
    redirection-status-codes
    client-error-status-codes
    server-error-status-codes))


(def ^{:added "0.0.7"}
  http-status->response-type
  "Mapping http status to response type."
  (set/map-invert response-type->http-status))



;;;;
;; Public API
;;;;

(defn to-status
  "Returns a http status by the given response type."
  {:added "0.0.7"}
  [response-type]
  (get response-type->http-status response-type ::unknown))


(defn to-type
  "Returns a response type by the given http status."
  {:added "0.0.7"}
  [http-status]
  (get http-status->response-type http-status ::unknown))
