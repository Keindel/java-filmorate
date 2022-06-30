package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

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

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.likeFromUser(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.unlikeFromUser(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getCountTop(@RequestParam(defaultValue = "10") int count) {
        return filmService.getCountTopIds(count).stream()
                .map(id -> {
                    try {
                        return filmService.getById(id);
                    } catch (UserNotFoundException | FilmNotFoundException | MpaNotFoundException |
                             GenreNotFoundException | DirectorNotFoundException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getSortedFilms(@PathVariable long directorId, @RequestParam String sortBy) throws ValidationException, DirectorNotFoundException {
        return filmService.getSortedFilms(directorId, sortBy);
    }

}