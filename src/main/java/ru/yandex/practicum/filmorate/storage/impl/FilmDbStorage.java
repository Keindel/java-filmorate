package ru.yandex.practicum.filmorate.storage.impl;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

public class FilmDbStorage implements FilmStorage {
    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return null;
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public void update(Film film) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
