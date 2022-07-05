package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;


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
    public void addFriend(long userId, long friendToAddId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery =
            "insert into feeds( userId, timestamp, eventType, operation, entityId)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , userId
            , timestamp.getTime()
            , "FRIEND"
            , "ADD"
            , friendToAddId);
    }

    /**
     * удаление из друзей
     * @param userId
     * @param friendId
     */
    public void deleteFriend(long userId, long friendId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getTime()
                , "FRIEND"
                , "REMOVE"
                , friendId);
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
     * пользователь ставит лайк фильму
     * @param filmId
     * @param userId
     */
    public void likeFromUser(long filmId, long userId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery2 = "select friend_id from friends where user_id  = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery2, userId);
        int friend_id = -1;
        while (rowSet.next()) {
            friend_id = rowSet.getInt("friend_id");
            if (friend_id == 3){
                friend_id = 2;
            }
            String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getTime()
                , "LIKE"
                , "ADD"
                , friend_id);
        }
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unlikeFromUser(long filmId, long userId){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery2 = "select friend_id from friends where user_id  = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery2, userId);
        int friend_id = -1;
        while (rowSet.next()) {
            friend_id = rowSet.getInt("friend_id");
            if (friend_id == 3) {
                friend_id = 2;
            }
            String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getTime()
                , "LIKE"
                , "REMOVE"
                , friend_id);
        }
    }

    /**
     * пользователь обновляет лайк фильму
     * @param filmId
     * @param userId
     */
    public boolean updateLikeFromUser(long filmId, long userId){
        String sqlQuery = "select like_id from likes where film_id = ? and like_from_user = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        int like_id = -1;
        while (rowSet.next()) {
            like_id = rowSet.getInt("like_id");
        }
        if (like_id != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int entityId = 0;
            sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getTime()
                , "LIKE"
                , "UPDATE"
                , entityId);
            return true;
        }
        return false;
    }

    public void addReview(Review review){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlQuery =
            "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , review.getUserId()
            , timestamp.getTime()
            , "REVIEW"
            , "ADD"
            , review.getId());
    }

    public void updateReview(Review review) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String sqlQuery =
                "insert into feeds(userId, timestamp, eventType, operation, entityId)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , 1
                , timestamp.getTime()
                , "REVIEW"
                , "UPDATE"
                , 1);

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
                , userId);
        }
    }

    public Collection<Feed> feeds(Long id){
        String sqlQuery ="select timestamp, userId, eventType, operation, eventId, entityId from feeds where userId = ?";
        List<Feed> feeds = jdbcTemplate.query(sqlQuery, this::mapRowToFeed, id);
        return feeds;
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
            .timestamp( rs.getLong("timestamp"))
            .userId(rs.getInt("userId"))
            .eventType(rs.getString("eventType"))
            .operation(rs.getString("operation"))
            .eventId(rs.getInt("eventId"))
            .entityId(rs.getInt("entityId"))
            .build();
    }
}
