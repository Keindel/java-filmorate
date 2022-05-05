package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    Set<Film> films = new HashSet<>();
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    @GetMapping()
    public Set<Film> findAll() {
        return films;
    }

    @PostMapping()
    public void create(@RequestBody Film film) throws FilmValidationException {
        if (films.contains(film)) throw new FilmValidationException();
        validateFilm(film);
        films.add(film);
        log.info("film created, total number = " + films.size());
    }

    private void validateFilm(Film film) throws FilmValidationException {
        if (film.getName().isBlank()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)
                || film.getDuration().isNegative()
                || film.getDuration().isZero()) {
            log.info("film validation fail");
            throw new FilmValidationException();
        }
    }

    @PutMapping
    public void update(@RequestBody Film film) throws FilmValidationException {
        validateFilm(film);
        films.remove(film);
        films.add(film);
        log.info("film updated or created with id = " + film.getId());
    }
}
