(ns unifier.response.http
  "Unified http responses."
  (:require
   [clojure.set :as set]))

;;;;
;; HTTP status codes
;;;;

(def ^{:added "0.0.7"}
  informational-status-codes
  "Informational `1xx` http status codes."
  {::continue            100
   ::switching-protocols 101
   ::processing          102
   ::early-hints         103})


(def ^{:added "0.0.7"}
  success-status-codes
  "Success `2xx` http status codes."
  {::ok                            200
   ::created                       201
   ::accepted                      202
   ::non-authoritative-information 203
   ::no-content                    204
   ::reset-content                 205
   ::partial-content               206
   ::multi-status                  207
   ::already-reported              208
   ::im-used                       226})


(def ^{:added "0.0.7"}
  redirection-status-codes
  "Redirection `3xx` http status codes."
  {::multiple-choice    300
   ::moved-permanently  301
   ::found              302
   ::see-other          303
   ::not-modified       304
   ::use-proxy          305
   ::switch-proxy       306 ;; Unused - This response code is no longer used; it is just reserved. It was used in a previous version of the HTTP/1.1 specification.
   ::temporary-redirect 307
   ::permanent-redirect 308})


(def ^{:added "0.0.7"}
  client-error-status-codes
  "Client error `4xx` http status codes."
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
   ::payload-too-large               413
   ::uri-too-long                    414
   ::unsupported-media-type          415
   ::range-not-satisfiable           416
   ::expectation-failed              417
   ::im-a-teapot                     418
   ::misdirected-request             421
   ::unprocessable-entity            422
   ::locked                          423
   ::failed-dependency               424
   ::too-early                       425
   ::upgrade-required                426
   ::precondition-required           428
   ::too-many-requests               429
   ::request-header-fields-too-large 431
   ::unavailable-for-legal-reasons   451})


(def ^{:added "0.0.7"}
  server-error-status-codes
  "Server error `5xx` http status codes."
  {::internal-server-error           500
   ::not-implemented                 501
   ::bad-gateway                     502
   ::service-unavailable             503
   ::gateway-timeout                 504
   ::http-version-not-supported      505
   ::variant-also-negotiates         506
   ::insufficient-storage            507
   ::loop-detected                   508
   ::not-extended                    510
   ::network-authentication-required 511})


(def ^{:added "0.0.7"}
  type->http
  "Map of response type and http status. E.g.  {::ok 200 ...}."
  (merge informational-status-codes
    success-status-codes
    redirection-status-codes
    client-error-status-codes
    server-error-status-codes))


(def ^{:added "0.0.7"}
  http->type
  "Map of http status and response type. E.g. {200 ::ok ...}."
  (set/map-invert type->http))


(def ^{:added "0.0.10"}
  allowed-types
  "Allowed response types."
  (set (keys type->http)))



;;;;
;; Public API
;;;;

(defn to-status
  "Returns a http status by the given response type."
  {:added "0.0.7"}
  [type]
  (get type->http type))


(defn to-type
  "Returns a response type by the given http status."
  {:added "0.0.7"}
  [http]
  (get http->type http))
