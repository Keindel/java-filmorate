package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
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
     *
     * @param userId
     * @param friendToAddId
     * @throws UserNotFoundException
     */
    public void addFriend(long userId, long friendToAddId) {
        String sqlQuery = "select friendship_id from friends where user_id = ? and friend_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendToAddId);
        int friendship_id = -1;
        while (rowSet.next()) {
            friendship_id = rowSet.getInt("friendship_id");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sqlQuery =
            "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                " values(?,?, ?, ?)";
        jdbcTemplate.update(sqlQuery
            , timestamp.getNanos()
            , friendship_id
            , 0
            , 0);
        sqlQuery = "select entity_id from entities where timestamp = ?";
        rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
        int entity_id = 0;
        while (rowSet.next()) {
            entity_id = rowSet.getInt("entity_id");
        }
        sqlQuery =
            "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , userId
            , timestamp.getNanos()
            , "FRIEND"
            , "ADD"
            , entity_id);
    }

    /**
     * удаление из друзей
     *
     * @param userId
     * @param friendId
     */
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "select friendship_id from friends where user_id = ? and friend_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        int friendship_id = -1;
        while (rowSet.next()) {
            friendship_id = rowSet.getInt("friendship_id");
        }
        if (friendship_id != 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , friendship_id
                , 0
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getNanos()
                , "FRIEND"
                , "REMOVE"
                , entity_id);
        }
    }

    /**
     * обновление друзей
     *
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
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , friendship_id
                , 0
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getNanos()
                , "FRIEND"
                , "UPDATE"
                , entity_id);
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
        String sqlQuery = "select like_id from likes where film_id = ? and like_from_user = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        int like_id = -1;
        while (rowSet.next()) {
            like_id = rowSet.getInt("like_id");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sqlQuery =
            "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                " values(?,?, ?, ?)";
        jdbcTemplate.update(sqlQuery
            , timestamp.getNanos()
            , 0
            , like_id
            , 0);
        sqlQuery = "select entity_id from entities where timestamp = ?";
        rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
        int entity_id = 0;
        while (rowSet.next()) {
            entity_id = rowSet.getInt("entity_id");
        }
        sqlQuery =
            "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , userId
            , timestamp.getNanos()
            , "LIKE"
            , "ADD"
            , entity_id);
    }

    /**
     * пользователь удаляет лайк
     * @param filmId
     * @param userId
     */
    public void unlikeFromUser(long filmId, long userId){
        String sqlQuery = "select like_id from likes where film_id = ? and like_from_user = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
        int like_id = -1;
        while (rowSet.next()) {
            like_id = rowSet.getInt("like_id");
        }
        if (like_id >= 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , 0
                , like_id
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getNanos()
                , "LIKE"
                , "REMOVE"
                , entity_id);
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
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , 0
                , like_id
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp.getNanos()
                , "LIKE"
                , "UPDATE"
                , entity_id);
            return true;
        }
        return false;
    }

    public void addReview(Review review){
        String sqlQuery = "select review_id from reviews where user_id = ? and film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, review.getUserId(), review.getFilmId());
        int review_id = -1;
        while (rowSet.next()) {
            review_id = rowSet.getInt("review_id");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sqlQuery =
            "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                " values(?,?, ?, ?)";
        jdbcTemplate.update(sqlQuery
            , timestamp.getNanos()
            , 0
            , 0
            , review_id);
        sqlQuery = "select entity_id from entities where timestamp = ?";
        rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
        int entity_id = 0;
        while (rowSet.next()) {
            entity_id = rowSet.getInt("entity_id");
        }
        sqlQuery =
            "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , review.getUserId()
            , timestamp.getNanos()
            , "REVIEW"
            , "ADD"
            , entity_id);
    }

    public void updateReview(Review review) {
        String sqlQuery = "select review_id from reviews where user_id = ? and film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, review.getUserId(), review.getFilmId());
        int review_id = -1;
        while (rowSet.next()) {
            review_id = rowSet.getInt("review_id");
        }
        if (review_id != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , 0
                , 0
                , review_id);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , review.getUserId()
                , timestamp.getNanos()
                , "REVIEW"
                , "UPDATE"
                , entity_id);
        }
    }

    public void deleteReview(Long reviewId) {
        String sqlQuery = "select user_id from reviews where review_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        int user_id = -1;
        while (rowSet.next()) {
            user_id = rowSet.getInt("user_id");
        }
        if (user_id != -1) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp.getNanos()
                , 0
                , 0
                , reviewId);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp.getNanos());
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,event_type, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , user_id
                , timestamp.getNanos()
                , "REVIEW"
                , "REMOVE"
                , entity_id);
        }
    }

    public Collection<Feed> feeds(Long id){
        String sqlQuery ="select timestamp, user_id,event_type, operation,event_id, entity_id from feeds where user_id = (" +
            "select * from friends where user_id = ? )";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        // timestamp, user_id, eventType, operation,event_Id, entity_Id
        return Feed.builder()
            .timestamp( rs.getInt("timestamp"))
            .user_id(rs.getInt("user_id"))
            .eventType(rs.getString("eventType"))
            .operation(rs.getString("operation"))
            .event_Id(rs.getInt("event_Id"))
            .entity_Id(rs.getInt("entity_Id"))
            .build();
    }
}
