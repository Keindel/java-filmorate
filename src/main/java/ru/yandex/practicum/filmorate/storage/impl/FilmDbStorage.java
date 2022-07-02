package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.impl.UserDbStorage.USERS_MATCHING_LIMIT;

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
        Film film = getWithoutGenresByIdOrThrowEx(id);
        setGenresToFilm(film);
        setUsersIdsLiked(film);
        return film;
    }

    private Film getWithoutGenresByIdOrThrowEx(Long id) throws FilmNotFoundException {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.MPA_ID, mpa.name" +
                " from films as f " +
                " left join MPA on f.mpa_id = mpa.ID" +
                " where film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new FilmNotFoundException();
        }
        return film;
    }

    private void setGenresToFilm(Film film) {
        String sqlGenresQuery = "select fgc.genre_id, gn.genre_name" +
                " from film_genre_coupling as fgc" +
                " left join genre_names as gn on fgc.genre_id = gn.genre_id" +
                " where fgc.film_id = ?";
        Set<Genre> genres = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGenresQuery, film.getId());
        while (rowSet.next()) {
            genres.add(new Genre(rowSet.getInt("genre_id"), rowSet.getString("genre_name")));
        }
        if (genres.size() > 0) {
            film.setGenres(genres);
        }
    }

    private void setUsersIdsLiked(Film film) {
        String sqlGetLikes = "select like_from_user" +
                " from likes where film_id = ?";
        Set<Long> usersIdsLiked = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetLikes, film.getId());
        while (rowSet.next()) {
            usersIdsLiked.add(rowSet.getLong("like_from_user"));
        }
        film.setUsersIdsLiked(usersIdsLiked);
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

        addFilmGenresToDb(film);
        return film;
    }

    private void addFilmGenresToDb(Film film) {
        String sqlGenresDelete = "delete from film_genre_coupling" +
                " where film_id = ?";
        jdbcTemplate.update(sqlGenresDelete, film.getId());
        String sqlGenresInsert = "insert into film_genre_coupling (film_id, genre_id)" +
                " values (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenresInsert,
                    film.getId(),
                    genre.getId()));
        }
    }

    @Override
    public void update(Film film) throws FilmNotFoundException {
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ?, MPA_ID = ?" +
                "where film_id = ?";
        int filmsUpdated = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        if (filmsUpdated == 0) throw new FilmNotFoundException();
        addFilmGenresToDb(film);
    }

    @Override
    public void deleteById(Long id) throws FilmNotFoundException {
        String sqlQuery = "delete from films where film_id = ?";
        int rowsUpdated = jdbcTemplate.update(sqlQuery, id);
        if (rowsUpdated == 0) throw new FilmNotFoundException();
    }

    public void likeFromUser(Long filmId, Long userId) throws FilmNotFoundException {
        getWithoutGenresByIdOrThrowEx(filmId);
        String sqlQuery = "insert into likes(film_id, like_from_user)" +
                " values(?, ?)";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws FilmNotFoundException {
        getWithoutGenresByIdOrThrowEx(filmId);
        String sqlQuery = "delete from likes" +
                " where film_id = ? and like_from_user = ?";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId);
    }

    public Collection<Film> getFilmsWithOneSideLikeFromOthers(Long userId) {
        String sqlFilmsOfUser = "(SELECT film_id FROM likes WHERE like_from_user = ?) ";
        String sqlGetTopMatchedUsers = "(SELECT " +
                " like_from_user AS other_user_id," +
                " FROM likes" +
                " WHERE film_id IN " + sqlFilmsOfUser +
                " GROUP BY other_user_id" +
                " ORDER BY COUNT (film_id) DESC" +
                " LIMIT ?)";
        String sqlGetRecommendedFilmsIds = "SELECT DISTINCT film_id" +
                " FROM likes" +
                " WHERE film_id NOT IN " + sqlFilmsOfUser +
                " AND like_from_user IN " + sqlGetTopMatchedUsers;
        Collection<Long> filmsIds = jdbcTemplate.queryForList(sqlGetRecommendedFilmsIds
                , Long.class
                , userId
                , userId
                , USERS_MATCHING_LIMIT);
        Collection<Film> films = filmsIds.stream().map(x -> {
            try {
                return getById(x);
            } catch (UserNotFoundException | FilmNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());
        return films;
    }

    public Collection<Film> getAllFilmsWithLikesFromUser(Long userid){
        String sqlGetAllFilmsWithLikesFromUser = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, mpa.name " +
                "FROM films as f " +
                "JOIN mpa ON f.mpa_id = mpa.id " +
                "JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE like_from_user = ? ";
        return jdbcTemplate.query(sqlGetAllFilmsWithLikesFromUser, this::mapRowToFilm, userid);
    }
    public List<Film> getPopularFilms(Integer count, Integer year, Integer genreId) {
        String sqlGetPopularFilms = "SELECT  f.*, m.NAME" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN likes AS l ON l.FILM_ID = f.FILM_ID" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY COUNT(l.FILM_ID) DESC LIMIT ? ";
        List<Film> films;
        if (Objects.nonNull(year) && Objects.nonNull(genreId)) {
            List<Film> years = getAllFilmsForYear(count, year);
            List<Film> genres = getAllFilmsForGenre(count, genreId);
            years.retainAll(genres);
            return years;
        } else if (Objects.nonNull(year)) {
            films = new ArrayList<>(getAllFilmsForYear(count, year));
        } else if (Objects.nonNull(genreId)) {
            films = new ArrayList<>(getAllFilmsForGenre(count, genreId));
        } else {
            films = new ArrayList<>(jdbcTemplate.query(sqlGetPopularFilms, this::mapRowToFilm, count));
        }
        return films;
    }

    private List<Film> getAllFilmsForYear(Integer count, Integer year) {
        String sqlGetPopularFilmsWithYear = "SELECT f.*, m.NAME" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN likes AS l ON l.FILM_ID = f.FILM_ID" +
                " WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ?" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY COUNT(l.FILM_ID) DESC LIMIT ? ";
        List<Film> films = jdbcTemplate.query(sqlGetPopularFilmsWithYear, this::mapRowToFilm, year, count);
        films.forEach(this::setGenresToFilm);
        return films;
    }

    private List<Film> getAllFilmsForGenre(Integer count, Integer genreId) {
        String sqlGetPopularFilmsWithGenre = "SELECT  f.*, m.name, gn.*" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN likes AS l ON l.FILM_ID = f.FILM_ID" +
                " LEFT JOIN film_genre_coupling AS fg ON f.FILM_ID = fg.FILM_ID" +
                " LEFT JOIN genre_names AS gn on gn.GENRE_ID = fg.GENRE_ID" +
                " WHERE gn.GENRE_ID = ?" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY COUNT(l.FILM_ID) DESC LIMIT ?";
         List<Film> films = jdbcTemplate.query(sqlGetPopularFilmsWithGenre, this::mapRowToFilm, genreId, count);
         films.forEach(this::setGenresToFilm);
         return films;
    }
}
