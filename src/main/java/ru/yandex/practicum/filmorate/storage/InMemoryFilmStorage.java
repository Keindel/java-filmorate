package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private static int nextId = 1;


    @Override
    public long getSize() {
        return films.size();
    }

    @Override
    public Film getById(long id) {
        return films.get(id);
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

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void deleteById(long id) {
        films.remove(id);
    }
}
