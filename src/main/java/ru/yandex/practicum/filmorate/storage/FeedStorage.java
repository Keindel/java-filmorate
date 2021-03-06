package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface FeedStorage {
    /**
     * добавление в друзья
     * @param userId
     * @param friendToAddId
     * @throws UserNotFoundException
     */
    void addFriend(long userId, long friendToAddId) throws UserNotFoundException;
    /**
     * удаление из друзей
     * @param userId
     * @param friendId
     */
    void deleteFriend(long userId, long friendId) throws UserNotFoundException;
    boolean updateFriend(long userId, long friendId);
    /**
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    void markFromUser(long filmId, long userId);

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    void unmarkFromUser(long filmId, long userId);
    boolean updateMarkFromUser(long filmId, long userId);
    void addReview(Review review);
    void updateReview(Review review);
    void deleteReview(Long reviewId);
    Collection<Feed> feeds(Long id);
}
