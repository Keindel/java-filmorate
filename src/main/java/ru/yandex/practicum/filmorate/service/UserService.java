package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final Storage<User> userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return userStorage.getById(id);
    }

    public long getSize() {
        return userStorage.getSize();
    }

    public User create(User user) {
        user = userStorage.create(user);
        return user;
    }

    public User update(User user) throws UserNotFoundException, FilmNotFoundException {
        userStorage.update(user);
        return user;
    }

    public void requestFriendship(Long userId, Long friendToAddId) throws UserNotFoundException, FilmNotFoundException {
        UserDbStorage userDbStorage = (UserDbStorage) userStorage;
        userDbStorage.requestFriendship(userId, friendToAddId);
    }

    public void deleteFriend(Long userId, Long friendToDellId) throws UserNotFoundException, FilmNotFoundException {
        UserDbStorage userDbStorage = (UserDbStorage) userStorage;
        userDbStorage.deleteFriendFromUser(userId, friendToDellId);
    }

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        long userIdWithLessFriends = Long.min(getFriendsNum(user1Id), getFriendsNum(user2Id));
        if (userIdWithLessFriends == 0) return Collections.emptyList();
        long otherUserId = user1Id - userIdWithLessFriends + user2Id ;

        return userStorage.getById(userIdWithLessFriends).getFriends().keySet()
                .stream()
                .filter(friendId -> {
                    try {
                        return userStorage.getById(otherUserId).getFriends().containsKey(friendId);
                    } catch (UserNotFoundException | FilmNotFoundException | MpaNotFoundException |
                             GenreNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private int getFriendsNum(Long userId) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        Map<Long, FriendshipStatus> friendsMap = userStorage.getById(userId).getFriends();
        if (friendsMap == null) return 0;
        return friendsMap.size();
    }

    public void deleteById(Long userId) throws UserNotFoundException, FilmNotFoundException {
        userStorage.deleteById(userId);
    }
}