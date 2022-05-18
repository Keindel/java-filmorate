package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Storage<Film> filmStorage;

    @Autowired
    public FilmService(Storage<Film> filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void likeFromUser(Long filmId, Long userId) {
        filmStorage.getById(filmId).addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) {
        filmStorage.getById(filmId).removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) {
        return filmStorage.getById(filmId).getUsersIdsLiked().size();
    }

    public Collection<Long> getTenTopIds() {
        return filmStorage.findAll().stream()
                .sorted((a, b) -> -getNumberOfLikes(a.getId()) + getNumberOfLikes(b.getId()))
                .limit(10)
                .map(Film::getId)
                .collect(Collectors.toList());
    }
}

/*
 * Создайте FilmService, который будет отвечать за операции с фильмами,
 *  — добавление и удаление лайка,
 *  вывод 10 наиболее популярных фильмов по количеству лайков.
 *  Пусть пока каждый пользователь может поставить лайк фильму только один раз.
 */