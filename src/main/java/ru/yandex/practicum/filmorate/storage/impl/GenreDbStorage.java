package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getCount() {
        String sqlQuery = "select COUNT(*) from genre_names";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public Genre getById(Long id) throws UserNotFoundException, FilmNotFoundException, GenreNotFoundException {
        String sqlQuery = "select genre_id, genre_name" +
                " from genre_names where genre_id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new GenreNotFoundException();
        }
        return genre;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "select genre_id, genre_name" +
                " from genre_names";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre create(Genre genre) {
        return null;
    }

    @Override
    public void update(Genre genre) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
