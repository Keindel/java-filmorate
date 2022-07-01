package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping()
    public Collection<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return directorService.getDirectorById(id);
    }

    @PostMapping()
    public Director create(@Valid @RequestBody @NonNull Director director) throws DirectorValidationException {
        director = directorService.create(director);
        log.info("director created with id = {}, number of directors = {}", director.getId(), directorService.getSize());
        return director;
    }

    @PutMapping
    public Director update(@Valid @RequestBody @NonNull Director director) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        directorService.update(director);
        log.info("director with id = {} updated or created", director.getId());
        return director;
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, DirectorNotFoundException, GenreNotFoundException {
        directorService.deleteDirectorById(id);
    }


}
