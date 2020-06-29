package com.plainid.assignment.controller;

import com.plainid.assignment.converter.mapper.PokemonIDMapper;
import com.plainid.assignment.converter.mapper.PokemonNameMapper;
import com.plainid.assignment.converter.mapper.TrainerRowMapper;
import com.plainid.assignment.dao.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
public class TrainerController {

    // returns the trainer's pokemons
    public List<String> getPokemons (String trainer_name) {
        return jdbcTemplate.query("SELECT *\n" +
                "FROM (SELECT * FROM TRAINER_POKEMONS WHERE TRAINER_NAME = '" + trainer_name + "') AS T1\n" +
                "JOIN (SELECT * FROM POKEMON) AS T2\n" +
                "ON T1.POKEMON_ID = T2.ID", new PokemonNameMapper());
    }

    private

    int timestamp = 0;

    @Autowired
    JdbcTemplate jdbcTemplate;

    // returns all the trainer's data
    @GetMapping("/{trainer_name}")
    public Trainer getTrainer(@PathVariable String trainer_name) {
        Trainer trainer = jdbcTemplate.queryForObject("SELECT * from TRAINER WHERE NAME='" + trainer_name + "'",new TrainerRowMapper());
        List<String> trainersPokemons = getPokemons(trainer_name);
        String[] pokemonNames = trainersPokemons.toArray(new String[trainersPokemons.size()]);
        trainer.setBag(pokemonNames);
        return trainer;
    }

    // adds a pokemon and removes (if needed) a pokemon by the minimum timestamp (FIFO), returns the trainer's pokemons
    @GetMapping("/{trainer_name}/catch/{pokemon_name}")
    public List<String> catchPokemon(@PathVariable String trainer_name, @PathVariable String pokemon_name) {
        List<String> trainersPokemons = getPokemons(trainer_name);
        int pokemonID = jdbcTemplate.queryForObject("SELECT ID FROM POKEMON WHERE NAME='" + pokemon_name + "'", new PokemonIDMapper()).intValue();
        if (trainersPokemons.size() == 3) {
            jdbcTemplate.update("UPDATE TRAINER_POKEMONS \n" +
                    "    SET POKEMON_ID=" + pokemonID + ", TIMESTAMP=" + ++timestamp + " \n" +
                    "WHERE TIMESTAMP=(\n" +
                    "    SELECT MIN(TIMESTAMP)\n" +
                    "    FROM \n" +
                    "    TRAINER_POKEMONS\n" +
                    "    WHERE TRAINER_NAME=\n'" + trainer_name +"')");
        }
        else {
            jdbcTemplate.update("INSERT INTO TRAINER_POKEMONS (TRAINER_NAME, POKEMON_ID, TIMESTAMP) VALUES ('" + trainer_name + "'," + pokemonID + "," + ++timestamp + ");");
        }
        trainersPokemons = getPokemons(trainer_name);
        return trainersPokemons;
    }
}
