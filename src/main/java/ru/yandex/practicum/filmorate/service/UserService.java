package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Storage<User> userDbStorage;

    public Collection<User> findAll() {
        return userDbStorage.findAll();
    }

    public User getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return userDbStorage.getById(id);
    }

    public long getSize() {
        return userDbStorage.getSize();
    }

    public User create(User user) {
        user = userDbStorage.create(user);
        return user;
    }

    public User update(User user) {
        userDbStorage.update(user);
        return user;
    }

    public void makeFriends(Long userId, Long friendToAddId) throws UserNotFoundException, FilmNotFoundException {
        User user = userDbStorage.getById(userId);
        User friend = userDbStorage.getById(friendToAddId);
        user.addFriend(friendToAddId);
        friend.addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendToDellId) throws UserNotFoundException, FilmNotFoundException {
        User user = userDbStorage.getById(userId);
        User friend = userDbStorage.getById(friendToDellId);
        user.deleteFriend(friendToDellId);
        friend.deleteFriend(userId);
    }

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) throws UserNotFoundException, FilmNotFoundException {
        long userIdWithLessFriends = Long.min(getFriendsNum(user1Id), getFriendsNum(user2Id));
        if (userIdWithLessFriends == 0) return Collections.emptyList();
        long otherUserId = user1Id - userIdWithLessFriends + user2Id ;

        return userDbStorage.getById(userIdWithLessFriends).getFriends().stream()
                .filter(friend -> {
                    try {
                        return userDbStorage.getById(otherUserId).getFriends().contains(friend);
                    } catch (UserNotFoundException | FilmNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private int getFriendsNum(Long userId) throws UserNotFoundException, FilmNotFoundException {
        Set<Long> friends = userDbStorage.getById(userId).getFriends();
        if (friends == null) return 0;
        return friends.size();
    }
}