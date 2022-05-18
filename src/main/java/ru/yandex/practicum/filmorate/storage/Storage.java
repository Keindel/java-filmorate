package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {

    long getSize();

    T getById(long id);

    Collection<T> findAll();

    T create(T t);

    void update(T t);

    void deleteById(long id);
}
