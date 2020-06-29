package com.plainid.assignment.dao;

/**
 * In our pokemon world there are 3 types of pokemons:
 * Grass, Water and fire.
 * Pokemon from particular type has advantage or weakness against other types
 */
public enum PokemonType {
    Grass("Grass"),
    Water("Water"),
    Fire("Fire");

    private final String type;


    PokemonType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
