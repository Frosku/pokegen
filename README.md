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

## Special Thanks

- [PokéAPI](https://pokeapi.co/) for providing all the base Pokémon data
used in Pokégen;
- [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/) for all of the
stat generation equations.
