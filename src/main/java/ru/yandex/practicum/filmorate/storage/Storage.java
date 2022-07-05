package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.*;

import java.util.Collection;

public interface Storage<T> {

    long getCount();

    T getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException;

    Collection<T> findAll();

    T create(T t) throws DirectorValidationException;

    void update(T t) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException;

    void deleteById(Long id) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException;
}
