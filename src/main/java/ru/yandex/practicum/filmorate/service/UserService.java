package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Storage<User> userStorage;

    @Autowired
    public UserService(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }


    public void makeFriends(Long userId, Long friendToAddId) {
        userStorage.getById(userId).addFriend(friendToAddId);
        userStorage.getById(friendToAddId).addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendToDelId) {
        userStorage.getById(userId).deleteFriend(friendToDelId);
        userStorage.getById(friendToDelId).deleteFriend(userId);
    }

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) {
        long userIdWithLessFriends = Long.min(getFriendsNum(user1Id), getFriendsNum(user2Id));
        long otherUserId = user1Id - userIdWithLessFriends + user2Id ;

        return userStorage.getById(userIdWithLessFriends).getFriends().stream()
                .filter(friend -> userStorage.getById(otherUserId).getFriends().contains(friend))
                .collect(Collectors.toList());
    }

    private int getFriendsNum(Long userId) {
        return userStorage.getById(userId).getFriends().size();
    }
}

/*
 * Создайте UserService, который будет отвечать за такие операции с пользователями,
 *  как добавление в друзья, удаление из друзей, вывод списка общих друзей.
 *  Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
 *  То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
 * */