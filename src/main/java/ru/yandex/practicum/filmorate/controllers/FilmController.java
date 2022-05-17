package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new ConcurrentHashMap<>();
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);
    private static int nextId = 1;

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody @NonNull Film film) throws FilmValidationException {
        validateFilm(film);
        film.setId(nextId);
        nextId++;
        films.put(film.getId(), film);
        log.info("film created with id = {}, number of films = {}", film.getId(), films.size());
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
        films.put(film.getId(), film);
        log.info("film updated or created with id = " + film.getId());
    }
}
