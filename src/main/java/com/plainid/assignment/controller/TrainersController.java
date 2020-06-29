package com.plainid.assignment.controller;

import com.plainid.assignment.converter.mapper.PokemonNameMapper;
import com.plainid.assignment.converter.mapper.TrainerRowMapper;
import com.plainid.assignment.dao.Trainer;
import com.plainid.assignment.dao.TrainerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainers")
public class TrainersController {
    private

    @Autowired
    JdbcTemplate jdbcTemplate;

    // returns all the trainers' data
    @GetMapping("")
    public List<Trainer> getTrainers() {
        List<Trainer> trainers = jdbcTemplate.query("SELECT * from TRAINER",new TrainerRowMapper());
        for(Trainer trainer : trainers) {
            String trainerName = trainer.getName();
            List<String> trainersPokemons = jdbcTemplate.query("SELECT *\n" +
                    "FROM (SELECT * FROM TRAINER_POKEMONS WHERE TRAINER_NAME = '" + trainerName + "') AS T1\n" +
                    "JOIN (SELECT * FROM POKEMON) AS T2\n" +
                    "ON T1.POKEMON_ID = T2.ID", new PokemonNameMapper());
            String[] pokemonNames = trainersPokemons.toArray(new String[trainersPokemons.size()]);
            trainer.setBag(pokemonNames);
        }
        TrainerList trainerList = new TrainerList();
        trainerList.setTrainers(trainers);
        return trainerList.getTrainers();
    }
}
