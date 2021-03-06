package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException, DirectorNotFoundException {
        return userStorage.getById(id);
    }

    public long getSize() {
        return userStorage.getCount();
    }

    public User create(User user) throws DirectorValidationException {
        user = userStorage.create(user);
        return user;
    }

    public User update(User user) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        userStorage.update(user);
        return user;
    }

    public void requestFriendship(Long userId, Long friendToAddId) throws UserNotFoundException {
        userStorage.requestFriendship(userId, friendToAddId);
    }

    public void deleteFriend(Long userId, Long friendToDellId) throws UserNotFoundException {
        userStorage.deleteFriendFromUser(userId, friendToDellId);
    }

    public Collection<Long> getMutualFriendsIds(Long userId, Long otherUserId) throws UserNotFoundException {
        return userStorage.getMutualFriendsIds(userId, otherUserId);
    }

    private int getFriendsNum(Long userId) throws UserNotFoundException {
        Map<Long, FriendshipStatus> friendsMap = userStorage.getById(userId).getFriends();
        if (friendsMap == null) return 0;
        return friendsMap.size();
    }

    public Collection<Film> recommendFilmsForUser(Long id) {
        return filmStorage.getRecommendedFilmsForUser(id);
    }

    public void deleteById(Long userId) throws UserNotFoundException, FilmNotFoundException {
        userStorage.deleteById(userId);
    }
}