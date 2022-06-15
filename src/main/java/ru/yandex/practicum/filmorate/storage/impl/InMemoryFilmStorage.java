package ru.yandex.practicum.filmorate.storage.impl;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private static int nextId = 1;

    @Override
    public long getSize() {
        return films.size();
    }

    @Override
    public Film getById(Long id) throws FilmNotFoundException {
        Film film = films.get(id);
        if (film == null) throw new FilmNotFoundException();
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId);
        nextId++;
        films.put(film.getId(), film);
        return film;
    }

    @SneakyThrows
    @Override
    public void update(Film film) {
        if (film.getId() <= 0 || !films.containsKey(film.getId())) throw new FilmNotFoundException();
        films.put(film.getId(), film);
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }
}
