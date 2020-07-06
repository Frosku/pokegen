(ns pokegen.api-test
  (:require  [clojure.test :refer :all]
             [pokegen.api :refer :all]
             [cheshire.core :as json]
             [clj-http.fake :refer :all]))


;; DATA DEFINITIONS FOR TESTING FAKE HTTP ROUTES, DO NOT MODIFY ;;


(def pokemon-results
  (->> [["eevee" 133] ["vaporeon" 134] ["jolteon" 135] ["flareon" 136]
        ["espeon" 196] ["umbreon" 197] ["leafeon" 470] ["glaceon" 471]
        ["sylveon" 700]]
       (mapv (fn [p] {:name (first p)
                     :url (build-api-url "pokemon" (second p))}))))

(defn pokemon-route-body []
  (json/generate-string {:results pokemon-results}))

(defn pokemon-route-res [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (pokemon-route-body)})

(defn vaporeon-lookup-route-body []
  (json/generate-string {:name "vaporeon"
                         :abilities [{:ability {:name "water-absorb"}
                                      :is_hidden false}
                                     {:ability {:name "hydration"}
                                      :is_hidden true}]
                         :stats [{:base_stat 130 :stat {:name "hp"}}
                                 {:base_stat 65 :stat {:name "attack"}}
                                 {:base_stat 60 :stat {:name "defense"}}
                                 {:base_stat 110
                                  :stat {:name "special-attack"}}
                                 {:base_stat 95
                                  :stat {:name "special-defense"}}
                                 {:base_stat 65 :stat {:name "speed"}}]
                         :types [{:type {:name "water"}}]
                         :moves [{:move {:name "tackle"}
                                  :version_group_details
                                  [{:level_learned_at 1
                                    :move_learn_method {:name "level-up"}
                                    :version_group {:name "sun-moon"}}]}]
                         :species {:name "vaporeon"}}))

(defn vaporeon-lookup-route-res [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (vaporeon-lookup-route-body)})

(defn vaporeon-species-route-body []
  (json/generate-string {:name "vaporeon"
                         :gender_rate 1
                         :habitat {:name "urban"}
                         :shape {:name "quadruped"}
                         :is_baby false
                         :color {:name "blue"}
                         :evolves_from_species {:name "eevee"}}))

(defn vaporeon-species-route-res [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (vaporeon-species-route-body)})


;; TESTS START HERE ;;


(deftest test-url-building
  (testing "We can get a user-specified endpoint"
    (is (= "https://pokeapi.co/api/v2/foo" (build-api-url "foo")))
    (is (= "https://pokeapi.co/api/v2/foo/bar" (build-api-url "foo" "bar"))))
  (testing "We can correctly generate the Pokemon endpoint"
    (is (= "https://pokeapi.co/api/v2/pokemon" (pokemon-endpoint)))))

(defn vaporeon-lookup-route-res [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (vaporeon-lookup-route-body)})

(deftest test-getting-pokemon-names
  (with-fake-routes-in-isolation
    {"https://pokeapi.co/api/v2/pokemon?limit=2000" pokemon-route-res}
    (testing "We can get all Pokemon names"
      (is (= ["eevee" "vaporeon" "jolteon" "flareon" "espeon"
              "umbreon" "leafeon" "glaceon" "sylveon"]
             (all-pokemon-names))))
    (testing "We can get random Pokemon names"
      (with-redefs [clojure.core/shuffle (fn [c] c)]
        (is (= ["eevee"] (random-pokemon-names)))
        (is (= ["eevee"] (random-pokemon-names 1)))
        (is (= ["eevee" "vaporeon"] (random-pokemon-names 2)))
        (is (= ["eevee" "vaporeon" "jolteon"]
               (random-pokemon-names 3)))))))

(deftest test-looking-up-pokemon
  (with-fake-routes-in-isolation
    {"https://pokeapi.co/api/v2/pokemon/vaporeon" vaporeon-lookup-route-res}
    (testing "We can get a Pokemon's information"
      (is (= "vaporeon"
             (:name (lookup-pokemon-by-name "vaporeon"))))
      (is (= {:hp 130 :attack 65 :defense 60 :special-attack 110
              :special-defense 95 :speed 65}
             (base-stats "vaporeon")))
      (is (= [:water] (types "vaporeon")))
      (is (= [{:move "tackle" :method "level-up" :min-level 1}]
             (learnable-moves "vaporeon")))
      (is (= [{:ability "water-absorb" :hidden false}
              {:ability "hydration" :hidden true}]
             (abilities "vaporeon"))))))

(deftest test-looking-up-pokemon-species
  (with-fake-routes-in-isolation
    {"https://pokeapi.co/api/v2/pokemon/vaporeon" vaporeon-lookup-route-res
     "https://pokeapi.co/api/v2/pokemon-species/vaporeon"
     vaporeon-species-route-res}
    (testing "We can get a Pokemon species' information"
      (is (= "vaporeon"
             (:name (lookup-pokemon-species-by-name "vaporeon"))))
      (is (= {:male 7 :female 1} (gender-distribution "vaporeon")))
      (is (= :urban (habitat "vaporeon")))
      (is (= :blue (color "vaporeon")))
      (is (= false (baby? "vaporeon")))
      (is (= :quadruped (anatomy "vaporeon")))
      (is (= "eevee" (pre-evolved-form "vaporeon"))))))
