package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmDbStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserDbStorage userStorage;
    @Qualifier("genreDbStorage")
    private final Storage<Genre> genreStorage;
    @Qualifier("mpaDbStorage")
    private final Storage<Mpa> mpaStorage;
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException {
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

    public Film update(Film film) throws FilmValidationException, UserNotFoundException, FilmNotFoundException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        userStorage.getWithoutFriendsByIdOrThrowEx(userId);
        filmStorage.likeFromUser(filmId, userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        userStorage.getWithoutFriendsByIdOrThrowEx(userId);
        filmStorage.unlikeFromUser(filmId, userId);
    }

    public Collection<Long> getCountTopIds(int count) {
        return filmStorage.getCountTopIds(count);
    }

    public Collection<Genre> getGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException {
        return genreStorage.getById(id);
    }

    public Collection<Mpa> getMpas() {
        return mpaStorage.findAll();
    }

    public Mpa getMpaById(long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException {
        return mpaStorage.getById(id);
    }

    public Collection<Film> getSortedFilms(long directorId, String sortBy) throws ValidationException, DirectorNotFoundException {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

}