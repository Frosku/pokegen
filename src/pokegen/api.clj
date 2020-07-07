(ns pokegen.api
  (:require [clojure.string :as str]
            [cheshire.core :refer :all]
            [clj-http.core :as httpcore]
            [clj-http.client :as http]
            [clj-http.conn-mgr :as conn]))

(def ^:const api-base "https://pokeapi.co/api/v2")

(def cm (conn/make-reusable-conn-manager {}))
(def client (httpcore/build-http-client {} false cm))

(defn build-api-url [& path]
  (str/join "/" (cons api-base path)))

(defn pokemon-endpoint [] (build-api-url "pokemon"))
(defn pokemon-lookup-endpoint [name] (build-api-url "pokemon" name))
(defn pokemon-species-endpoint [name] (build-api-url "pokemon-species" name))

(defn all-pokemon-names
  "Returns a vector of the names of all Pokemon, in lowercase.

   (all-pokemon-names)
   => [\"bulbasaur\" \"ivysaur\" ...]"
  []
  (->> (-> (pokemon-endpoint)
          (http/get {:connection-manager cm :http-client client
                     :cache true :accept :json :query-params {"limit" 2000}})
          (:body)
          (parse-string true)
          (:results))
       (mapv #(:name %))))

(defn random-pokemon-names
  "Returns n random Pokemon names in lowercase, if the parameter n is
   omitted, returns a single random Pokemon name.

   (random-pokemon-names 2)
   => [\"hitmonchan\" \"jirachi\"]"
  ([] (random-pokemon-names 1))
  ([n] (->> (all-pokemon-names)
            shuffle
            (take n))))

(defn lookup-pokemon-by-name
  "Returns detailed information about a Pokemon from its name, in the
   format provided by Pokeapi. Consider using the helper functions
   below for less data.

   (lookup-pokemon-by-name \"vaporeon\")
   => {:name \"vaporeon\" ...}"
  [name]
  (-> name
     (pokemon-lookup-endpoint)
     (http/get {:connection-manager cm :http-client client
                :cache true :accept :json})
     (:body)
     (parse-string true)))

(defn lookup-pokemon-species-by-name
  "Returns detailed information about a Pokemon species from the
   Pokemon's name, in the format provided by Pokeapi. Consider using
   the helper functions below for less data.

  (lookup-pokemon-species-by-name \"vaporeon\")
  => {:name \"vaporeon\"}"
  [name]
  (-> name
     (lookup-pokemon-by-name)
     (:species)
     (:name)
     (pokemon-species-endpoint)
     (http/get {:connection-manager cm :http-client client
                :cache true :accept :json})
     (:body)
     (parse-string true)))

(defn base-stats
  "Returns the base stats for a Pokemon as a map.

   (base-stats \"vaporeon\")
   => {:hp 130 :attack 65 ...}"
  [name]
  (->> name
       (lookup-pokemon-by-name)
       (:stats)
       (mapv (fn [s] {(keyword (:name (:stat s)))
                     (:base_stat s)}))
       (apply merge)))

(defn types
  "Returns a vector of types for a Pokemon.

   (types \"vaporeon\")
   => [\"water\"]"
  [name]
  (->> name
       (lookup-pokemon-by-name)
       (:types)
       (mapv (fn [t] (keyword (:name (:type t)))))))

(defn learnable-moves
  "Returns all moves which a Pokemon can learn, each represented as
   a map with the move name, the earliest level it can be learned,
   and the method by which the Pokemon learns the move (as of the
   latest game it can be learned in).

   (learnable-moves \"vaporeon\")
   => [{:move \"tackle\" :method \"level-up\" :min-level 1}]"
  [name]
  (let [moves (:moves (lookup-pokemon-by-name name))]
    (loop [move (first moves) rest (next moves) acc []]
      (if (nil? move)
        acc
        (let [new (->> (:version_group_details move)
                       last)]
          (recur (first rest)
                 (next rest)
                 (conj acc {:move (-> move (:move) (:name))
                            :method (-> new (:move_learn_method) (:name))
                            :min-level (-> new (:level_learned_at))})))))))

(defn abilities
  "Returns a vector of abilities which a Pokemon can have, along with
   whether the ability is hidden.

   (abilities \"vaporeon\")
   => [{:ability \"water-absorb\" :hidden false} ...]"
  [name]
  (->> name
       (lookup-pokemon-by-name)
       (:abilities)
       (mapv (fn [a] {:ability (:name (:ability a))
                     :hidden (:is_hidden a)}))))

(defn gender-distribution
  "Returns a map with gender distribution for a given Pokemon. If the
   Pokemon has no gender, returns nil.

   (gender-distribution \"vaporeon\")
   => {:male 7 :female 1}

   (gender-distribution \"jirachi\")
   => nil"
  [name]
  (let [gr (->> name (lookup-pokemon-species-by-name) (:gender_rate))]
    (if (= gr -1)
      nil
      {:male (- 8 gr) :female gr})))

(defn habitat
  "Returns the habitat for a Pokemon as a keyword.

   (habitat \"vaporeon\")
   => :urban"
  [name]
  (-> name (lookup-pokemon-species-by-name) (:habitat) (:name) (keyword)))

(defn color
  "Returns the Pokemon's predominant color as a keyword.

   (color \"vaporeon\")
   => :blue"
  [name]
  (-> name (lookup-pokemon-species-by-name) (:color) (:name) (keyword)))

(defn anatomy
  "Returns the Pokemon's anatomical shape as a keyword.

   (shape \"vaporeon\")
   => :quadruped"
  [name]
  (-> name (lookup-pokemon-species-by-name) (:shape) (:name) (keyword)))

(defn baby?
  "Determines whether the Pokemon is a baby.

   (baby? \"vaporeon\")
   => false"
  [name]
  (-> name (lookup-pokemon-species-by-name) (:is_baby)))

(defn pre-evolved-form
  "Returns the name of the pre-evolved form of the Pokemon, or returns
   nil if the Pokemon has no pre-evolved form.

   (evolves-from \"vaporeon\")
   => \"eevee\""
  [name]
  (let [pre-evolved (:evolves_from_species
                     (lookup-pokemon-species-by-name name))]
    (if (= nil pre-evolved) nil (:name pre-evolved))))

(defn display-name
  "Returns the display name for the Pokemon, in English.

   (display-name \"vaporeon\")
   => \"Vaporeon\""
  [name]
  (->> name
       (lookup-pokemon-species-by-name)
       (:names)
       (filterv #(= "en" (:name (:language %))))
       (first)
       (:name)))

(defn egg-groups
  "Returns the egg groups for the Pokemon.

   (egg-group \"vaporeon\")
   => [:ground]"
  [name]
  (->> name
       (lookup-pokemon-species-by-name)
       (:egg_groups)
       (mapv #(keyword (:name %)))))
