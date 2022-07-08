package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.xml.bind.ValidationException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.impl.UserDbStorage.USERS_MATCHING_LIMIT;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getCount() {
        String sqlQuery = "select COUNT(*) from films";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        Film film = getWithoutGenresByIdOrThrowEx(id);
        setDirectors(film);
        setGenresToFilm(film);
        setUsersIdsMarks(film);
        setFilmRating(film);
        return film;
    }

    private void setDirectors(Film film) {
        String sqlQuery1 = "SELECT count(*) FROM film_director_coupling where film_id = ?";
        long result = jdbcTemplate.queryForObject(sqlQuery1, Long.class, film.getId());
        if (result < 1) {
            film.setDirectors(new ArrayList<>());
        } else {
            String sqlQuery = "select DN.director_id, DN.director_name from director_names DN left outer join film_director_coupling F on DN.director_id = F.director_id where F.film_id = ?";
            List<Director> directors;
            directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId());
            film.setDirectors(directors);
        }
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("director_id"), rs.getString("director_name"));
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

    private void setUsersIdsMarks(Film film) {
        String sqlGetMarks = "select mark_from_user, mark" +
                " from marks where film_id = ?";
        Map<Long, Integer> usersIdsMarks = new HashMap<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetMarks, film.getId());
        while (rowSet.next()) {
            usersIdsMarks.put(rowSet.getLong("mark_from_user")
                    , rowSet.getInt("mark"));
        }
        film.setUsersIdsMarks(usersIdsMarks);
    }

    private void setFilmRating(Film film){
        String sqlGetRating = "SELECT AVG(m.mark) FROM marks AS m WHERE m.film_id = ?";
        double rating = jdbcTemplate.queryForObject(sqlGetRating, Double.class, film.getId());
        film.setRating(rating);
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

        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        films.forEach(this::setDirectors);
        return films;
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
        addFilmDirectorstoDB(film);
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
        addFilmDirectorstoDB(film);
    }

    private void addFilmDirectorstoDB(Film film) {
        String sqlDirectorsDelete = "delete from film_director_coupling" +
                " where film_id = ?";
        jdbcTemplate.update(sqlDirectorsDelete, film.getId());
        String sqlDirectorInsert = "insert into film_director_coupling (film_id, director_id)" +
                " values (?, ?)";
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> jdbcTemplate.update(sqlDirectorInsert,
                    film.getId(),
                    director.getId()));
        }
    }

    @Override
    public void deleteById(Long id) throws FilmNotFoundException {
        String sqlQuery = "delete from films where film_id = ?";
        int rowsUpdated = jdbcTemplate.update(sqlQuery, id);
        if (rowsUpdated == 0) throw new FilmNotFoundException();
    }

    public void markFromUser(Long filmId, Long userId, int mark) throws FilmNotFoundException {
        getWithoutGenresByIdOrThrowEx(filmId);
        String sqlQuery = "insert into marks(film_id, mark_from_user, mark)" +
                " values(?, ?, ?)";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId
                , mark);
    }

    public void unmarkFromUser(Long filmId, Long userId) throws FilmNotFoundException {
        getWithoutGenresByIdOrThrowEx(filmId);
        String sqlQuery = "delete from marks" +
                " where film_id = ? and mark_from_user = ?";
        jdbcTemplate.update(sqlQuery
                , filmId
                , userId);
    }

    public Collection<Film> getRecommendedFilmsForUser(Long userId) {
        String sqlAllMarkedFilmsOfUser = "(SELECT m1.film_id FROM marks AS m1 WHERE mark_from_user = ?)";
        String sqlGoodFilmsOfUserWithMarks = "(SELECT m2.film_id, m2.mark FROM marks AS m2" +
                " WHERE m2.mark_from_user = ? AND m2.mark > 5) AS good_films_user ";
        String sqlGetTopMatchedUsers = "(SELECT m3.mark_from_user AS other_user_id" +
                " FROM marks AS m3 JOIN " + sqlGoodFilmsOfUserWithMarks + " ON m3.film_id = good_films_user.film_id" +
                " WHERE m3.mark > 5 AND ABS(m3.mark - good_films_user.mark) <= 1" +
                " AND m3.mark_from_user <> ?" +
                " GROUP BY other_user_id" +
                " ORDER BY COUNT (m3.film_id) DESC" +
                " LIMIT ?)";
        String sqlGetRecommendedFilmsIds = "SELECT DISTINCT m4.film_id" +
                " FROM marks AS m4" +
                " WHERE m4.film_id NOT IN " + sqlAllMarkedFilmsOfUser +
                " AND m4.mark_from_user IN " + sqlGetTopMatchedUsers +
                " GROUP BY m4.film_id" +
                " HAVING AVG(m4.mark) >= 6";
        Collection<Long> filmsIds = jdbcTemplate.queryForList(sqlGetRecommendedFilmsIds,
                Long.class, userId, userId, userId, USERS_MATCHING_LIMIT);
        return filmsIds.stream().map(x -> {
            try {
                return getById(x);
            } catch (UserNotFoundException | FilmNotFoundException | DirectorNotFoundException e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<Film> getPopularFilms(Integer count, Integer year, Integer genreId) {
        String sqlGetPopularFilms = "SELECT  f.*, m.NAME" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN marks ON marks.FILM_ID = f.FILM_ID" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY AVG(marks.MARK) DESC LIMIT ? ";
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
            films.forEach(film -> {
                setDirectors(film);
                setGenresToFilm(film);
                setUsersIdsMarks(film);
                setFilmRating(film);
            } );
        }
        return films;
    }

    private List<Film> getAllFilmsForYear(Integer count, Integer year) {
        String sqlGetPopularFilmsWithYear = "SELECT f.*, m.NAME" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN marks ON marks.FILM_ID = f.FILM_ID" +
                " WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ?" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY AVG(marks.MARK) DESC LIMIT ? ";
        List<Film> films = jdbcTemplate.query(sqlGetPopularFilmsWithYear, this::mapRowToFilm, year, count);
        films.forEach(film -> {
            setDirectors(film);
            setGenresToFilm(film);
            setUsersIdsMarks(film);
            setFilmRating(film);
        } );
        return films;
    }

    private List<Film> getAllFilmsForGenre(Integer count, Integer genreId) {
        String sqlGetPopularFilmsWithGenre = "SELECT  f.*, m.name, gn.*" +
                " FROM films AS f" +
                " JOIN mpa AS m on m.ID = f.MPA_ID" +
                " LEFT JOIN marks ON marks.FILM_ID = f.FILM_ID" +
                " LEFT JOIN film_genre_coupling AS fg ON f.FILM_ID = fg.FILM_ID" +
                " LEFT JOIN genre_names AS gn on gn.GENRE_ID = fg.GENRE_ID" +
                " WHERE gn.GENRE_ID = ?" +
                " GROUP BY f.FILM_ID" +
                " ORDER BY AVG(marks.MARK) DESC LIMIT ?";
         List<Film> films = jdbcTemplate.query(sqlGetPopularFilmsWithGenre, this::mapRowToFilm, genreId, count);
        films.forEach(film -> {
            setDirectors(film);
            setGenresToFilm(film);
            setUsersIdsMarks(film);
            setFilmRating(film);
        } );
         return films;
    }

    public Collection<Film> getDirectorFilms(long directorId, String sortBy) throws ValidationException, DirectorNotFoundException {
        String sqlQuery1 = "select count(*) from director_names where director_id = ?";
        long result = jdbcTemplate.queryForObject(sqlQuery1, Long.class, directorId);
        if (result != 1) throw new DirectorNotFoundException();

        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.MPA_ID, mpa.name " +
                "from films as f " +
                "left join MPA on f.mpa_id = mpa.ID " +
                "left join film_director_coupling as fdc ON f.film_id = fdc.film_id " +
                "left join marks ON f.film_id = marks.film_id " +
                "where fdc.director_id = ? " +
                "order by avg(marks.mark)";
        List<Film> films;
        if (sortBy.equals("marks")) {
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);

            films.forEach(film -> {
                setDirectors(film);
                setGenresToFilm(film);
                setUsersIdsMarks(film);
                setFilmRating(film);
            } );
        } else if (sortBy.equals("year")) {
            sqlQuery = sqlQuery + "order by release_date ASC;";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
            films.forEach(film -> {
                setDirectors(film);
                setGenresToFilm(film);
                setUsersIdsMarks(film);
                setFilmRating(film);
            } );
        } else {
            throw new ValidationException("Такой вариант сортировки не предусмотрен");
        }
        return films;
    }

    public List<Film> getAllFilmsWithMarksFromUser(Long userid) {
        String sqlGetAllFilmsWithMarksFromUser = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, mpa.name " +
                "FROM films as f " +
                "JOIN mpa ON f.mpa_id = mpa.id " +
                "JOIN marks ON f.film_id = marks.film_id " +
                "WHERE mark_from_user = ? ";
        return jdbcTemplate.query(sqlGetAllFilmsWithMarksFromUser, this::mapRowToFilm, userid);
    }

    public Collection<Film> getSearch(String query, String by) {
        query = query.toLowerCase();
        Collection<Film> films;
        List<Long> filmIds;
        switch (by) {
            case "director":
                String sqlQueryDirector = "SELECT f.film_id" +
                        " FROM films AS f" +
                        " RIGHT JOIN film_director_coupling AS fdc ON f.film_id = fdc.film_id" +
                        " RIGHT JOIN director_names AS dn ON fdc.director_id = dn.director_id" +
                        " LEFT JOIN marks AS l ON l.FILM_ID = f.FILM_ID" +
                        " WHERE lower(dn.director_name) LIKE CONCAT('%', ?, '%')" +
                        " GROUP BY f.Film_id, dn.DIRECTOR_NAME" +
                        " ORDER BY COUNT(l.FILM_ID) DESC";
                filmIds = jdbcTemplate.queryForList(sqlQueryDirector, Long.class, query);
                break;
            case "title":
                String sqlQueryTitle = "SELECT f.Film_id" +
                        " FROM films AS f" +
                        " LEFT JOIN marks AS l ON l.FILM_ID = f.FILM_ID" +
                        " WHERE lower(f.name) LIKE CONCAT('%', ?, '%')" +
                        " GROUP BY f.Film_id" +
                        " ORDER BY COUNT(l.FILM_ID) DESC";
                filmIds = jdbcTemplate.queryForList(sqlQueryTitle, Long.class, query);
                break;
            default:
                String sqlQueryAnyway = "SELECT f.film_id" +
                        " FROM films AS f" +
                        " LEFT JOIN film_director_coupling as fdc ON f.film_id = fdc.film_id" +
                        " LEFT JOIN director_names AS dn ON dn.director_id = fdc.director_id" +
                        " LEFT JOIN marks AS l ON l.FILM_ID = f.FILM_ID" +
                        " WHERE LOWER(f.name) LIKE CONCAT('%', ?, '%')" +
                        " OR LOWER(dn.director_name) LIKE CONCAT('%', ?, '%')" +
                        " GROUP BY f.Film_id, dn.DIRECTOR_NAME" +
                        " ORDER BY COUNT(l.FILM_ID) DESC";
                filmIds = jdbcTemplate.queryForList(sqlQueryAnyway, Long.class, query, query);
                break;
        }
        films = filmIds.stream()
                .map(filmId -> {
                    try {
                        return getById(filmId);
                    } catch (UserNotFoundException | FilmNotFoundException | DirectorNotFoundException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
        return films;
    }
}
