(ns pokegen.describe
  (:require [clojure.string :as str]))

(defn describe-pokemon
  "Generates a description for a single Pokemon.

   (describe-pokemon p1)
   => nil"
  [pokemon]
  (format "a level %s %s %s with a %s nature"
          (:level pokemon)
          (if (nil? (:gender pokemon)) "" (name (:gender pokemon)))
          (:human-readable-species pokemon)
          (name (:nature pokemon))))

(defn describe-team
  "Prints a description for a sequence of Pokemon.

   (describe-team [p1 p2 p3])
   => nil"
  [team]
  (->> team
       (mapv (fn [pokemon] (describe-pokemon pokemon)))
       (str/join ", ")
       (format "Your team has the following Pokemon: %s.")
       (println)))
