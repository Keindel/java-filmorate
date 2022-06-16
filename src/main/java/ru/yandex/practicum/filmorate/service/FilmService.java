package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final Storage<Film> filmDbStorage;
    private final Storage<User> userDbStorage;
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmDbStorage.findAll();
    }

    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return filmDbStorage.getById(id);
    }

    public Film create(Film film) throws FilmValidationException {
        validateFilm(film);
        film = filmDbStorage.create(film);
        return film;
    }

    private void validateFilm(Film film) throws FilmValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("film validation fail");
            throw new FilmValidationException();
        }
    }

    public long getSize() {
        return filmDbStorage.getSize();
    }

    public Film update(Film film) throws FilmValidationException {
        validateFilm(film);
        filmDbStorage.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmDbStorage.getById(filmId);
        User userExistant = userDbStorage.getById(userId);
        filmExistant.addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmDbStorage.getById(filmId);
        User userExistant = userDbStorage.getById(userId);
        filmExistant.removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmDbStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Long> getCountTopIds(int count) {
        return filmDbStorage.findAll().stream()
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
}