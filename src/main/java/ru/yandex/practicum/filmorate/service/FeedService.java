package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserDbStorage userStorage;

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
        userStorage.getWithoutFriendsByIdOrThrowEx(userId);
        userStorage.getWithoutFriendsByIdOrThrowEx(friendToAddId);
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
    public void markFromUser(long filmId, long userId) {
        feedStorage.markFromUser(filmId, userId);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unmarkFromUser(long filmId, long userId) {
        feedStorage.unmarkFromUser(filmId, userId);
    }

    /**
     * пользователь обновляет лайк фильму
     * @param filmId
     * @param userId
     */
    public boolean updateMarkFromUser(long filmId, long userId) {
        return feedStorage.updateMarkFromUser( filmId, userId);
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
