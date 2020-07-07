(ns pokegen.core
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.cli :refer [parse-opts]]
            [pokegen.spec :as ps]
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

(def cli-options
  [["-p" "--pokemon NUMBER" "Number of Pokemon to generate (default: 6)"
    :default 6
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 12) "Must be a number between 1 and 12"]]
   [nil "--debug" "Show debug info"]])

(defn -main
  [& args]
  (let [opts (parse-opts args cli-options)]
    (if (not (:debug (:options opts)))
      (-> opts (:options) (:pokemon) (random-team) (pd/describe-team))
      (-> opts (:options) (:pokemon) (random-team) (pprint)))))
