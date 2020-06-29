package com.plainid.assignment;

import com.plainid.assignment.dao.*;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PokemonTest {

    @LocalServerPort
    private int port = 8080;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void testGetAllPokemons(){
        PokemonList pokemonList = restTemplate.getForEntity("http://localhost:" + port + "/pokemon/list",
                PokemonList.class).getBody();
        assertThat(pokemonList).isNotNull();
        assertThat(pokemonList.getPokemons()).isNotNull();
        assertThat(pokemonList.size()).isEqualTo(15);
    }

    // not enough Pokemons case
    @Test
    public void testBattle(){
        BattleResult battle = restTemplate.getForEntity("http://localhost:" + port + "/battle/Jonathan/Jacob",
                BattleResult.class).getBody();
        assertThat(battle).isNotNull();
        assertThat(battle.getStatus()).isNotNull();
        assertThat(battle.getMessage()).isNotNull();
        assertThat(battle.getStatus()).isEqualTo("error");
        assertThat(battle.getMessage()).isEqualTo("canceled");
    }

    // draw case
    @Test
    public void testBattle2(){
        BattleResult battle = restTemplate.getForEntity("http://localhost:" + port + "/battle/Shawn/Tom",
                BattleResult.class).getBody();
        assertThat(battle).isNotNull();
        assertThat(battle.getStatus()).isNotNull();
        assertThat(battle.getMessage()).isNotNull();
        assertThat(battle.getStatus()).isEqualTo("success");
        assertThat(battle.getMessage()).isEqualTo("draw");
    }

    @Test
    public void testBattle3(){
        BattleResult battle = restTemplate.getForEntity("http://localhost:" + port + "/battle/Terry/Ryan",
                BattleResult.class).getBody();
        assertThat(battle).isNotNull();
        assertThat(battle.getStatus()).isNotNull();
        assertThat(battle.getMessage()).isNotNull();
        assertThat(battle.getStatus()).isEqualTo("success");
        assertThat(battle.getMessage()).isEqualTo("Terry wins");
    }


    @Test
    public void testTrainers(){
        ResponseEntity<List<Trainer>> response =
                restTemplate.exchange("http://localhost:" + port + "/trainers",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Trainer>>() {
                        });
        List<Trainer> trainers = response.getBody();
        boolean contains1 = false;
        boolean contains2 = false;
        int count = 0;
        for(Trainer t : trainers) {
          if(t.getName().equals("notSupposedToBeThere"))
              contains1 = true;
            if(t.getName().equals("Jonathan"))
                contains2 = true;
            count++;
        }

        assertThat(count).isEqualTo(7);
        assertFalse(contains1);
        assertTrue(contains2);
    }

    // FIFO case
    @Test
    public void testCatch(){
        ResponseEntity<List<String>> response =
                restTemplate.exchange("http://localhost:" + port + "/trainer/Terry/catch/Lapras",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {
                        });
        List<String> pokemons = response.getBody();
        assertFalse(pokemons.contains("Bulbasaur"));
        assertTrue(pokemons.contains("Lapras"));
    }

}
