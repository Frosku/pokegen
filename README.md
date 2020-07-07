![Pokegen Logo](res/logo.png)

Pokégen is a Pokémon generation library, designed to show an example of
what Disultory can do.

## Usage

To generate a team with very basic descriptions (this is very much still
a WIP) -- optional n as team size (default 6):

```
$ lein run
$ lein run n
```

Runtimes can be long, it calls an external API for all data lookups.

### Example Runs

There's a lot more data being generated & allocated under the hood, but this is good for debug:

```
❯ lein run 2
Your team has the following Pokemon: a level 5 
female Ninetales with a timid nature, a level 15  
Ho-Oh with a brave nature.
❯ lein run 2
Your team has the following Pokemon: a level 2 
male Magmortar with a bold nature, a level 13 
male Chinchou with a bold nature.
❯ lein run 3
Your team has the following Pokemon: a level 6 
female Illumise with a mild nature, a level 15 
male Venipede with a timid nature, a level 6 
female Pumpkaboo with a timid nature.
❯ lein run 4
Your team has the following Pokemon: a level 11 
female Yanma with a calm nature, a level 13 male 
Vulpix with a lax nature, a level 4  Poipole with 
a bashful nature, a level 6 male Lickilicky with
a docile nature.
❯ lein run 5
Your team has the following Pokemon: a level 12
male Diggersby with a docile nature, a level 16 
male Dartrix with a timid nature, a level 18 
female Togedemaru with a lonely nature, a level 
1  Baltoy with a naughty nature, a level 5 male 
Lycanroc with a quiet nature.
```

## Special Thanks

- [PokéAPI](https://pokeapi.co/) for providing all the base Pokémon data
used in Pokégen;
- [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/) for all of the
stat generation equations.
