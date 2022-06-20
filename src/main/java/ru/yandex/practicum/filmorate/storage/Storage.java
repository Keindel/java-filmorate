package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.Collection;

public interface Storage<T> {

    long getSize();

    T getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException;

    Collection<T> findAll();

    T create(T t);

    void update(T t) throws UserNotFoundException, FilmNotFoundException;

    void deleteById(Long id) throws UserNotFoundException, FilmNotFoundException;
}
