package com.plainid.assignment.controller;

import com.plainid.assignment.converter.mapper.PokemonNameMapper;
import com.plainid.assignment.converter.mapper.PokemonRawMapper;
import com.plainid.assignment.dao.BattleResult;
import com.plainid.assignment.dao.Pokemon;
import com.plainid.assignment.dao.PokemonType;
import com.plainid.assignment.dao.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/battle")
public class BattleController {
    // returns the trainer's pokemons
    public List<Pokemon> getPokemons (String trainer_name) {
        return jdbcTemplate.query("SELECT *\n" +
                "FROM (SELECT * FROM TRAINER_POKEMONS WHERE TRAINER_NAME = '" + trainer_name + "') AS T1\n" +
                "JOIN (SELECT * FROM POKEMON) AS T2\n" +
                "ON T1.POKEMON_ID = T2.ID", new PokemonRawMapper());
    }

    // returns -1/0/1 according to losing, tie and winning
    public int battlePokemon(PokemonType pokemon1, PokemonType pokemon2) {
        if (pokemon1.toString().equals("Fire")) {
            return pokemon2.toString().equals("Fire") ? 0 : (pokemon2.toString().equals("Water") ? -1 : 1);
        }
        if (pokemon1.toString().equals("Water")) {
            return pokemon2.toString().equals("Water") ? 0 : (pokemon2.toString().equals("Grass") ? -1 : 1);
        }
        return pokemon2.toString().equals("Grass") ? 0 : (pokemon2.toString().equals("Fire") ? -1 : 1);
    }

    private

    @Autowired
    JdbcTemplate jdbcTemplate;

    // updates the winner's level and returns the battle's details
    @GetMapping("/{trainer1_name}/{trainer2_name}")
    public BattleResult getTrainer(@PathVariable String trainer1_name, @PathVariable String trainer2_name) {
        List<Pokemon> trainer1Pokemons = getPokemons(trainer1_name);
        List<Pokemon> trainer2Pokemons = getPokemons(trainer2_name);
        if (! (trainer1Pokemons.size() == 3 && trainer2Pokemons.size() == 3)) {
            return new BattleResult("error", "canceled");
        }
        int trainer1Score = 0;
        for (int i=0; i<3; i++) {
            trainer1Score += battlePokemon(trainer1Pokemons.get(i).getType(), trainer2Pokemons.get(i).getType());
        }
        if (trainer1Score == 0) {
            jdbcTemplate.update("UPDATE TRAINER SET LEVEL=LEVEL+1 WHERE NAME='" + trainer1_name + "';");
            jdbcTemplate.update("UPDATE TRAINER SET LEVEL=LEVEL+1 WHERE NAME='" + trainer2_name + "';");
            return new BattleResult("success", "draw");
        } else if (trainer1Score > 0) {
            jdbcTemplate.update("UPDATE TRAINER SET LEVEL=LEVEL+2 WHERE NAME='" + trainer1_name + "';");
            return new BattleResult("success", trainer1_name + " wins");
        } else {
            jdbcTemplate.update("UPDATE TRAINER SET LEVEL=LEVEL+2 WHERE NAME='" + trainer2_name + "';");
            return new BattleResult("success", trainer2_name + " wins");
        }
    }
}
