package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    @Qualifier("userDbStorage")
    private final Storage<User> userStorage;

    /**
     * Возвращает ленту событий пользователя
     * @return
     */
    public Collection<Feed> feeds(Long id){
        return feedStorage.feeds(id);
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
        feedStorage.addFriend(userId, friendToAddId);
    }

    /**
     * удаление из друзей
     * @param userId
     * @param friendId
     */
    public void deleteFriend(long userId, long friendId) {
        feedStorage.deleteFriend(userId, friendId);
    }

    /**
     * обновление друзей
     * @param userId
     * @param friendId
     */
    public boolean updateFriend(long userId, long friendId) {
        return feedStorage.updateFriend(userId, friendId);
    }

    /**
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    public void likeFromUser( long filmId, long userId) {
        feedStorage.likeFromUser( filmId, userId);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unlikeFromUser( long filmId, long userId) {
        feedStorage.unlikeFromUser( filmId, userId);
    }

    /**
     * пользователь обновляет лайк фильму
     * @param filmId
     * @param userId
     */
    public boolean updateLikeFromUser( long filmId, long userId) {
        return feedStorage.updateLikeFromUser( filmId, userId);
    }

    public void addReview(Review review) {
        feedStorage.addReview(review);
    }

    public void updateReview(Review review) {
        feedStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId){
        feedStorage.deleteReview(reviewId);
    }
}
