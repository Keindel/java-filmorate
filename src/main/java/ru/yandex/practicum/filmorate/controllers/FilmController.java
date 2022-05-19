package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    public Film getFilm(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @PostMapping()
    public Film create(Film film) throws FilmValidationException {
        film = filmService.create(film);
        log.info("film created with id = {}, number of films = {}", film.getId(), filmService.getSize());
        return film;
    }

    @PutMapping
    public Film update(Film film) throws FilmValidationException {
        filmService.update(film);
        log.info("film with id {} updated", film.getId());
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.likeFromUser(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.unlikeFromUser(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getCountTop(@RequestParam(defaultValue = "10") int count) {
        return filmService.getCountTopIds(count).stream()
                .map(filmService::getById)
                .collect(Collectors.toList());
    }
}