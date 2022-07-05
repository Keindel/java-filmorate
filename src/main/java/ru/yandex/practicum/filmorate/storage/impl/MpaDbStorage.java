package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getCount() {
        String sqlQuery = "select COUNT(*) from mpa";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public Mpa getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException {
        String sqlQuery = "select id, name" +
                " from mpa where id = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new MpaNotFoundException();
        }
        return mpa;
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }

    @Override
    public Collection<Mpa> findAll() {
            String sqlQuery = "select id, name" +
                    " from mpa";
            return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa create(Mpa mpa) {
        return null;
    }

    @Override
    public void update(Mpa mpa) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
