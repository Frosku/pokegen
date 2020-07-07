![Pokegen Logo](res/logo.png)

Pokégen is a Pokémon generation library, designed to show an example of
what Disultory can do.

## Usage

To generate a team with very basic descriptions (this is very much still
a WIP) -- optional n as team size (default 6):

```
$ lein run
$ lein run --pokemon n
```

Runtimes can be long, it calls an external API for all data lookups.

### Example Runs

There's a lot more data being generated & allocated under the hood, but this is OK for output:

```
❯ lein run --pokemon 2
Your team has the following Pokemon: a level 5 
female Ninetales with a timid nature, a level 15  
Ho-Oh with a brave nature.
❯ lein run --pokemon 3
Your team has the following Pokemon: a level 6 
female Illumise with a mild nature, a level 15 
male Venipede with a timid nature, a level 6 
female Pumpkaboo with a timid nature.
❯ lein run --pokemon 4
Your team has the following Pokemon: a level 11 
female Yanma with a calm nature, a level 13 male 
Vulpix with a lax nature, a level 4  Poipole with 
a bashful nature, a level 6 male Lickilicky with
a docile nature.
❯ lein run --pokemon 5
Your team has the following Pokemon: a level 12
male Diggersby with a docile nature, a level 16 
male Dartrix with a timid nature, a level 18 
female Togedemaru with a lonely nature, a level 
1  Baltoy with a naughty nature, a level 5 male 
Lycanroc with a quiet nature.
```

If you want to see what's being generated:

```
({:habitat nil,
  :anatomy :ball,
  :color :brown,
  :pre-evolved-form nil,
  :egg-groups [:mineral],
  :species "minior-green-meteor",
  :human-readable-species "Minior",
  :nature :relaxed,
  :level 20,
  :gender nil,
  :baby? false,
  :base-stats
  {:hp 60,
   :attack 60,
   :defense 100,
   :special-attack 60,
   :special-defense 100,
   :speed 60},
  :ivs
  {:hp 17,
   :attack 10,
   :defense 21,
   :speed 11,
   :special-attack 16,
   :special-defense 18},
  :stats
  {:hp 57,
   :attack 31,
   :defense 54,
   :special-attack 32,
   :special-defense 48,
   :speed 28}}
 {:habitat :grassland,
  :anatomy :upright,
  :color :yellow,
  :pre-evolved-form "elekid",
  :egg-groups [:humanshape],
  :species "electabuzz",
  :human-readable-species "Electabuzz",
  :nature :bashful,
  :level 2,
  :gender :male,
  :baby? false,
  :base-stats
  {:hp 65,
   :attack 83,
   :defense 57,
   :special-attack 95,
   :special-defense 85,
   :speed 105},
  :ivs
  {:hp 28,
   :attack 11,
   :defense 19,
   :speed 12,
   :special-attack 26,
   :special-defense 1},
  :stats
  {:hp 15,
   :attack 8,
   :defense 7,
   :special-attack 9,
   :special-defense 8,
   :speed 9}})
```

## Special Thanks

- [PokéAPI](https://pokeapi.co/) for providing all the base Pokémon data
used in Pokégen;
- [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/) for all of the
stat generation equations.
