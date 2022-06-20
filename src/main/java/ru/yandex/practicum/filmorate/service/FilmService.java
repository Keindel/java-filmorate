package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final Storage<Film> filmStorage;
    @Qualifier("userDbStorage")
    private final Storage<User> userStorage;
    @Qualifier("genreDbStorage")
    private final Storage<Genre> genreStorage;
    @Qualifier("mpaDbStorage")
    private final Storage<Mpa> mpaStorage;
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return filmStorage.getById(id);
    }

    public Film create(Film film) throws FilmValidationException {
        validateFilm(film);
        film = filmStorage.create(film);
        return film;
    }

    private void validateFilm(Film film) throws FilmValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("film validation fail");
            throw new FilmValidationException();
        }
    }

    public long getSize() {
        return filmStorage.getSize();
    }

    public Film update(Film film) throws FilmValidationException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
//        filmExistant.addLike(userId);
        FilmDbStorage filmDbStorage = (FilmDbStorage) filmStorage;
        filmDbStorage.likeFromUser(filmId, userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
//        filmExistant.removeLike(userId);
        FilmDbStorage filmDbStorage = (FilmDbStorage) filmStorage;
        filmDbStorage.unlikeFromUser(filmId, userId);
    }

    private int getNumberOfLikes(Long filmId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Long> getCountTopIds(int count) {
        return filmStorage.findAll().stream()
                .sorted((a, b) -> {
                    try {
                        return -getNumberOfLikes(a.getId()) + getNumberOfLikes(b.getId());
                    } catch (UserNotFoundException | FilmNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .limit(count)
                .map(Film::getId)
                .collect(Collectors.toList());
    }

    public Collection<Genre> getGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(long id) throws UserNotFoundException, FilmNotFoundException {
        return genreStorage.getById(id);
    }

    public Collection<Mpa> getMpas() {
        return mpaStorage.findAll();
    }

    public Mpa getMpaById(long id) throws UserNotFoundException, FilmNotFoundException {
        return mpaStorage.getById(id);
    }
}