package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getSize() {
        String sqlQuery = "select COUNT(*) from films";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.MPA_ID, mpa.name" +
                " from films as f " +
                " left join MPA on f.mpa_id = mpa.ID" +
                " where film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa.name")))
                .build();
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.MPA_ID, mpa.name" +
                " from films as f " +
                " left join MPA on f.mpa_id = mpa.ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into films (film_id, name, description, release_date, duration, MPA_ID)" +
                "values (DEFAULT, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        long idFromDb = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(idFromDb);
        return film;
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ?, MPA_ID = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
