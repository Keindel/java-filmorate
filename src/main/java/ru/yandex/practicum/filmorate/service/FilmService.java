package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;

    @Autowired
    public FilmService(Storage<Film> filmStorage, Storage<User> userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFromUser(Long filmId, Long userId) {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
        filmExistant.addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) {
        Film filmExistant = filmStorage.getById(filmId);
        User userExistant = userStorage.getById(userId);
        filmExistant.removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) {
        Film filmExistant = filmStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Long> getCountTopIds(int count) {
        return filmStorage.findAll().stream()
                .sorted((a, b) -> -getNumberOfLikes(a.getId()) + getNumberOfLikes(b.getId()))
                .limit(count)
                .map(Film::getId)
                .collect(Collectors.toList());
    }
}