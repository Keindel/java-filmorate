package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;


@Slf4j
@Component("feedDbStorage")
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * добавление в друзья
     * @param userId
     * @param friendToAddId
     * @throws UserNotFoundException
     */
    public void addFriend(long userId, long friendToAddId) throws UserNotFoundException {
        String action = "ADD";
        addOrRemoveFriend(userId, friendToAddId, action);
    }

    /**
     * удаление из друзей
     * @param userId
     * @param friendId
     */
    public void deleteFriend(long userId, long friendId) throws UserNotFoundException {
        String action = "REMOVE";
        addOrRemoveFriend(userId, friendId, action);
    }

    /**
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    public void markFromUser(long filmId, long userId) {
        String action = "ADD";
        addOrRemoveMarkFromUser(userId, filmId, action);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unmarkFromUser(long filmId, long userId) {
        String action = "REMOVE";
        addOrRemoveMarkFromUser(userId, filmId, action);
    }

    public void addReview(Review review) {
        String action = "ADD";
        addOrUpdateReview(review, action);
    }

    public void updateReview(Review review) {
        String action = "UPDATE";
        addOrUpdateReview(review, action);
    }

    /**
     * обновление друзей
     * @param userId
     * @param friendId
     */
    public boolean updateFriend(long userId, long friendId) {
        String sqlQuery = "select friendship_id from friends where user_id = ? and friend_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        int friendship_id = -1;
        while (rowSet.next()) {
            friendship_id = rowSet.getInt("friendship_id");
        }
        if (friendship_id != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int entityId = 0;
            while (rowSet.next()) {
                entityId = rowSet.getInt("entityId");
            }
            sqlQuery =
                    "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                            " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                    , userId
                    , timestamp.getTime()
                    , "FRIEND"
                    , "UPDATE"
                    , entityId);
            return true;
        }
        return false;
    }

    /**
     * пользователь обновляет лайк фильму
     * @param filmId
     * @param userId
     */
    public boolean updateMarkFromUser(long filmId, long userId) {
        String sqlQuery = "select mark_id from marks where film_id = ? and mark_from_user = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        int mark_id = -1;
        while (rowSet.next()) {
            mark_id = rowSet.getInt("mark_id");
        }
        if (mark_id != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int entityId = 0;
            sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                    , userId
                    , timestamp.getTime()
                    , "MARK"
                    , "UPDATE"
                    , entityId);
            return true;
        }
        return false;
    }


    public void deleteReview(Long reviewId) {
        String sqlQuery = "select user_id from reviews where review_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        int userId = -1;
        while (rowSet.next()) {
            userId = rowSet.getInt("user_id");
        }
        if (userId != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getTime()
                , "REVIEW"
                , "REMOVE"
                , reviewId);
        }
    }

    public Collection<Feed> feeds(Long id){
        String sqlQuery ="select timestamp, userId, eventType, operation, eventId, entityId from feeds where userId = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, id);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
            .timestamp(rs.getLong("timestamp"))
            .userId(rs.getInt("userId"))
            .eventType(rs.getString("eventType"))
            .operation(rs.getString("operation"))
            .eventId(rs.getInt("eventId"))
            .entityId(rs.getInt("entityId"))
            .build();
    }

    private void addOrRemoveMarkFromUser (long userId, long filmId, String action){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery2 = "select user_id from users where user_id  = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery2, userId);
        int user_id = -1;
        while (rowSet.next()) {
            user_id = rowSet.getInt("user_id");
            String sqlQuery =
                    "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                            " values(?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                    , userId
                    , timestamp.getTime()
                    , "MARK"
                    , action
                    , filmId);
        }
    }

    private void addOrUpdateReview(Review review, String action){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                        " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
                , review.getUserId()
                , timestamp.getTime()
                , "REVIEW"
                , action
                , review.getReviewId());
    }

    private void addOrRemoveFriend(long userId, long friendId, String action) throws UserNotFoundException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                        "values(select user_id " +
                        "from users " +
                        "where user_id = ?, ?, ?, ?," +
                        "select user_id " +
                        "from users " +
                        "where user_id = ?) ";
        try {
            jdbcTemplate.update(sqlQuery
                    , userId
                    , timestamp.getTime()
                    , "FRIEND"
                    , action
                    , friendId);
        } catch (IncorrectResultSizeDataAccessException e){
            throw new UserNotFoundException();
        }
    }
}
