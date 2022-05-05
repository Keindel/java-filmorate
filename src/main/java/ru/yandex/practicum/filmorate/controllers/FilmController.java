package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping()
    public void create(@RequestBody Film film) throws FilmValidationException {
        validateFilm(film);
        if (films.containsKey(film.getId())) throw new FilmValidationException();
        films.put(film.getId(), film);
        log.info("film created, total number = " + films.size());
    }

    private void validateFilm(Film film) throws FilmValidationException {
        if (film == null
                || film.getName().isBlank()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)
                || film.getDuration().isNegative()
                || film.getDuration().isZero()) {
            log.warn("film validation fail");
            throw new FilmValidationException();
        }
    }

    @PutMapping
    public void update(@RequestBody Film film) throws FilmValidationException {
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("film updated or created with id = " + film.getId());
    }
}
