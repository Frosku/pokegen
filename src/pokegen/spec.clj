(ns pokegen.spec
  (:require [pokegen.api :refer :all]
            [disultory.core :as d]
            [disultory.spec :as ds]
            [disultory.decision :as dd]))

(def nature-stat->flavor {:attack :spicy
                          :defense :sour
                          :special-attack :dry
                          :special-defense :bitter
                          :speed :sweet})

(def natures {:hardy {:buffs nil :nerfs nil}
              :docile {:buffs nil :nerfs nil}
              :bashful {:buffs nil :nerfs nil}
              :quirky {:buffs nil :nerfs nil}
              :serious {:buffs nil :nerfs nil}
              :lonely {:buffs :attack :nerfs :defense}
              :adamant {:buffs :attack :nerfs :special-attack}
              :naughty {:buffs :attack :nerfs :special-defense}
              :brave {:buffs :attack :nerfs :speed}
              :bold {:buffs :defense :nerfs :attack}
              :impish {:buffs :defense :nerfs :special-attack}
              :lax {:buffs :defense :nerfs :special-defense}
              :relaxed {:buffs :defense :nerfs :speed}
              :modest {:buffs :special-attack :nerfs :attack}
              :mild {:buffs :special-attack :nerfs :defense}
              :rash {:buffs :special-attack :nerfs :special-defense}
              :quiet {:buffs :special-attack :nerfs :speed}
              :calm {:buffs :special-defense :nerfs :attack}
              :gentle {:buffs :special-defense :nerfs :defense}
              :careful {:buffs :special-defense :nerfs :special-attack}
              :sassy {:buffs :special-defense :nerfs :speed}
              :timid {:buffs :speed :nerfs :attack}
              :hasty {:buffs :speed :nerfs :defense}
              :jolly {:buffs :speed :nerfs :special-attack}
              :naive {:buffs :speed :nerfs :special-defense}})

(defn pokemon-species-attribute
  "Gets a random Pokemon species."
  []
  (ds/fn-attribute :species (fn [_] (first (random-pokemon-names)))))

(defn individual-values-attribute
  "Attributes for :individual-values for :hp, :attack, :defense, :speed,
   :special-attack and :special-defense, each ranging from 0 to 31."
  []
  (->> [:hp :attack :defense :speed :special-attack :special-defense]
       (mapv (fn [iv] [iv [(ds/dice + 1 32) (ds/dice - 1)]]))
       (ds/dice-attribute :ivs)))

(defn level-attribute
  "Generates a random level between 1 and n, the default value for n
   is 10."
  ([] (level-attribute 20))
  ([n] (ds/random-attribute :level [(ds/dice + 1 n)])))

(defn nature-attribute
  "Generates a random nature from the list of natures in Pokemon."
  []
  (->> natures
       keys
       (mapv (fn [n] [n 1]))
       (ds/distinct-attribute :nature)))

(defn with-species-defined-conditional
  [spec attribute]
  (ds/with-conditional spec (fn [spec] (not (nil? (:species spec)))) attribute))

(defn with-species-based-gender-attribute
  [spec]
  (with-species-defined-conditional spec
    (ds/fn-attribute
     :gender
     (fn [spec] (let [gd (gender-distribution (:species spec))]
                 (if (= gd nil) nil
                     (-> gd (dd/get-weighted-distribution) (rand-nth))))))))

(defn with-species-based-base-stats-attribute
  [spec]
  (with-species-defined-conditional spec
    (ds/fn-attribute :base-stats (fn [spec] (base-stats (:species spec))))))

(defn with-species-attributes
  "Takes species attributes and sets them as conditional attributes."
  [spec]
  (-> spec
     (with-species-based-gender-attribute)
     (with-species-based-base-stats-attribute)
     (with-species-defined-conditional
       (ds/fn-attribute :anatomy (fn [spec] (anatomy (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :pre-evolved-form
                        (fn [spec] (pre-evolved-form (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :baby? (fn [spec] (baby? (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :color (fn [spec] (color (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :habitat (fn [spec] (habitat (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :egg-groups (fn [spec] (egg-groups (:species spec)))))
     (with-species-defined-conditional
       (ds/fn-attribute :human-readable-species
                        (fn [spec] (display-name (:species spec)))))))

(defn calc-hp
  [base iv level]
  (-> 2 (* base) (+ iv) (* level) (/ 100) (+ 10) (+ level) (Math/floor) (int)))

(defn calc-stat
  [base iv level with-buff with-nerf]
  (let [multiplier (cond (= true with-buff) 1.1 (= true with-nerf) 0.9 :else 1)]
    (-> 2 (* base) (+ iv) (* level) (/ 100) (+ 5) (* multiplier) (Math/floor) (int))))

(defn with-stats
  [spec]
  (ds/with-conditional
    spec
    (fn [spec] (and (not (nil? (:base-stats spec)))
                   (not (nil? (:nature spec)))
                   (not (nil? (:ivs spec)))))
    (ds/fn-attribute
     :stats
     (fn [spec]
       (let [base-stats (:base-stats spec)
             ivs (:ivs spec)
             nature-buffs (:buffs (get natures (:nature spec)))
             nature-nerfs (:nerfs (get natures (:nature spec)))]
         (->> [:hp :attack :defense :special-attack :special-defense :speed]
              (mapv (fn [s]
                      {s (if (= s :hp)
                           (calc-hp (:hp base-stats) (:hp ivs) (:level spec))
                           (calc-stat (get base-stats s)
                                      (get ivs s)
                                      (:level spec)
                                      (= nature-buffs s)
                                      (= nature-nerfs s)))}))
              (apply merge)))))))
