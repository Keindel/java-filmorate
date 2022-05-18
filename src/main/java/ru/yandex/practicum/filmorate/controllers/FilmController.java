package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);
    private final Storage<Film> filmStorage;
    private final FilmService filmService;
    @Autowired
    public FilmController(Storage<Film> filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping()
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        validateFilm(film);
        film = filmStorage.create(film);
        log.info("film created with id = {}, number of films = {}", film.getId(), filmStorage.getSize());
        return film;
    }

    private void validateFilm(@NonNull Film film) throws FilmValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("film validation fail");
            throw new FilmValidationException();
        }
    }

    @PutMapping
    public void update(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("film with id {} updated or created", film.getId());
    }
}
