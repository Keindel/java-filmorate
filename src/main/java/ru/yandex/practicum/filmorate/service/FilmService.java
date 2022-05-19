package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public Film create(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        validateFilm(film);
        film = filmStorage.create(film);
        return film;
    }

    private void validateFilm(@NonNull Film film) throws FilmValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("film validation fail");
            throw new FilmValidationException();
        }
    }

    public long getSize() {
        return filmStorage.getSize();
    }

    public Film update(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
        filmExistant.addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
        filmExistant.removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) {
        Film filmExistant = filmStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Long> getCountTopIds(int count) {
        return filmStorage.findAll().stream()
                .sorted((a, b) -> -getNumberOfLikes(a.getId()) + getNumberOfLikes(b.getId()))
                .limit(count)
                .map(Film::getId)
                .collect(Collectors.toList());
    }
}