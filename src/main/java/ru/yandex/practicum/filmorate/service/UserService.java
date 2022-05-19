package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Storage<User> userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public long getSize() {
        return userStorage.getSize();
    }

    public User create(@Valid @RequestBody @NonNull User user) {
        user = userStorage.create(user);
        return user;
    }

    public User update(@Valid @RequestBody @NonNull User user) {
        userStorage.update(user);
        return user;
    }

    public void makeFriends(Long userId, Long friendToAddId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendToAddId);
        user.addFriend(friendToAddId);
        friend.addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendToDellId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendToDellId);
        user.deleteFriend(friendToDellId);
        friend.deleteFriend(userId);
    }

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) {
        long userIdWithLessFriends = Long.min(getFriendsNum(user1Id), getFriendsNum(user2Id));
        if (userIdWithLessFriends == 0) return Collections.emptyList();
        long otherUserId = user1Id - userIdWithLessFriends + user2Id ;

        return userStorage.getById(userIdWithLessFriends).getFriends().stream()
                .filter(friend -> userStorage.getById(otherUserId).getFriends().contains(friend))
                .collect(Collectors.toList());
    }

    private int getFriendsNum(Long userId) {
        Set<Long> friends = userStorage.getById(userId).getFriends();
        if (friends == null) return 0;
        return friends.size();
    }
}