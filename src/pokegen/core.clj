(ns pokegen.core
  (:require [pokegen.spec :as ps]
            [pokegen.describe :as pd]
            [disultory.core :as d]
            [disultory.spec :as ds])
  (:gen-class))

(defn random-pokemon
  []
  (-> (d/blank-specification)
     (ds/with (ps/pokemon-species-attribute))
     (ds/with (ps/individual-values-attribute))
     (ds/with (ps/level-attribute))
     (ds/with (ps/nature-attribute))
     (ps/with-species-attributes)
     (ps/with-stats)
     (d/generate)))

(defn random-team
  [n]
  (->> random-pokemon
       repeatedly
       (take n)))

(defn exec
  ([] (exec 6))
  ([n] (->> (random-team (int n))
            (pd/describe-team))))

(defn -main
  [& args]
  (->> args
       (apply exec)))
