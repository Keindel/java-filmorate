package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FeedService feedService;

    @GetMapping()
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException {
        return filmService.getById(id);
    }

    @PostMapping()
    public Film create(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        film = filmService.create(film);
        log.info("film created with id = {}, number of films = {}", film.getId(), filmService.getSize());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody @NonNull Film film) throws FilmValidationException, UserNotFoundException, FilmNotFoundException {
        filmService.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    /**
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.likeFromUser(filmId, userId);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.unlikeFromUser(filmId, userId);
        feedService.unlikeFromUser(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> mostPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                       @RequestParam(required = false) Integer year,
                                       @RequestParam(required = false) Integer genreId) {
        return filmService.mostPopularFilms(count, year, genreId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getSortedFilms(@PathVariable long directorId, @RequestParam String sortBy) throws ValidationException, DirectorNotFoundException {
        return filmService.getSortedFilms(directorId, sortBy);
    }


    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) throws FilmNotFoundException {
        filmService.deleteById(filmId);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) throws UserNotFoundException {
        return filmService.getCommonFilms(userId, friendId);
    }
    @GetMapping("/search")
    public Collection<Film> getSearch(@RequestParam String query, @RequestParam String by) {
        return filmService.getSearch(query, by);
    }
}