package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DirectorValidationException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static long id = 1;

    private long directorIdCounter() {
        return id++;
    }

    @Override
    public long getSize() {
        String sqlQuery = "select COUNT(*) from director_names";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public Director getById(Long id) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        String sqlQuery = "select director_id, director_name" +
                " from director_names where director_id = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DirectorNotFoundException();
        }
        return director;
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("director_id"), rs.getString("director_name"));
    }

    @Override
    public Collection<Director> findAll() {
        if (!doesDirectorExist()) {
            return new ArrayList<Director>();
        }
        String sqlQuery2 = "select director_id, director_name from director_names";
        return jdbcTemplate.query(sqlQuery2, this::mapRowToDirector);
    }

    @Override
    public Director create(Director director) throws DirectorValidationException {
        if (director.getName().isBlank()) throw new DirectorValidationException();

        String sqlQuery = "insert into director_names (director_id, director_name) values (?, ?)";
        director.setId(directorIdCounter());
        jdbcTemplate.update(sqlQuery, director.getId(), director.getName());
        return director;
    }

    @Override
    public void update(Director director) throws DirectorNotFoundException {
        String sqlGetDirector = "select * from director_names where director_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetDirector, director.getId());
        if (!rowSet.isBeforeFirst()) throw new DirectorNotFoundException();
        while (rowSet.next()) {
        String sqlQuery2 = "update director_names set director_name = ? where director_id = ?";
        jdbcTemplate.update(sqlQuery2,
                director.getName(), director.getId());
        }
    }

    private boolean doesDirectorExist() {
        String sqlQuery1 = "select count(*) from director_names ";
        long result = jdbcTemplate.queryForObject(sqlQuery1, Long.class);
        return result > 0;
    }

    @Override
    public void deleteById(Long id) throws DirectorNotFoundException {
        String sqlGetDirectors = "select * from director_names where director_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetDirectors, id);
        if (!rowSet.isBeforeFirst()) throw new DirectorNotFoundException();
        while (rowSet.next()) {
            String sqlQuery2 = "delete from director_names where director_id = ?";
            jdbcTemplate.update(sqlQuery2, id);
        }
    }
}

