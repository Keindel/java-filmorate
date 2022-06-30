package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedDbStorage;
    @Qualifier("userDbStorage")
    private final Storage<User> userStorage;

    /**
     * Возвращает ленту событий пользователя
     * @return
     */
    public Collection<Feed> feeds(){
        List<Feed> feeds = new ArrayList<>();

        return feeds;
    }

    /**
     * добавление в друзья
     * @param userId
     * @param friendToAddId
     * @throws UserNotFoundException
     */
    public void addFriend(long userId, long friendToAddId) throws UserNotFoundException {
        UserDbStorage userDbStorage = (UserDbStorage) userStorage;
        userDbStorage.getWithoutFriendsByIdOrThrowEx(userId);
        userDbStorage.getWithoutFriendsByIdOrThrowEx(friendToAddId);
        feedDbStorage.addFriend(userId, friendToAddId);
    }

    /**
     * удаление из друзей
     * @param userId
     * @param friendId
     */
    public void deleteFriend(long userId, long friendId) {
        feedDbStorage.deleteFriend(userId, friendId);
    }

    /**
     * обновление друзей
     * @param userId
     * @param friendId
     */
    public boolean updateFriend(long userId, long friendId) {
        return feedDbStorage.updateFriend(userId, friendId);
    }

    /**
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    public void likeFromUser( long filmId, long userId) {
        feedDbStorage.likeFromUser( filmId, userId);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unlikeFromUser( long filmId, long userId) {
        feedDbStorage.unlikeFromUser( filmId, userId);
    }

    /**
     * пользователь обновляет лайк фильму
     * @param filmId
     * @param userId
     */
    public boolean updateLikeFromUser( long filmId, long userId) {
        return feedDbStorage.updateLikeFromUser( filmId, userId);
    }
}
