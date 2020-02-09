(ns user
  "Development helper functions."
  (:require
    [cljs.repl.node :as rn]
    [cider.piggieback :as pb]))

(defn cljs-repl []
  (pb/cljs-repl (rn/repl-env)))
