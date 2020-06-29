package com.plainid.assignment.converter.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PokemonIDMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer pokemonID = rs.getInt("ID");

        return pokemonID;
    }
}
