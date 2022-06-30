package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.Timestamp;


@Slf4j
@Component("filmDbStorage")
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
            , timestamp
            , friendship_id
            , 0
            , 0);
        sqlQuery = "select entity_id from entities where timestamp = ?";
        rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
        int entity_id = 0;
        while (rowSet.next()) {
            entity_id = rowSet.getInt("entity_id");
        }
        sqlQuery =
            "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , userId
            , timestamp
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
        if (friendship_id >= 0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            sqlQuery =
                "insert into entities(timestamp,friendship_id, like_id, review_id)" +
                    " values(?,?, ?, ?)";
            jdbcTemplate.update(sqlQuery
                , timestamp
                , friendship_id
                , 0
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp
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
                , timestamp
                , friendship_id
                , 0
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp
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
        String sqlQuery = "select like_id from likes where film_id = ? and user_id = ?";
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
            , timestamp
            , 0
            , like_id
            , 0);
        sqlQuery = "select entity_id from entities where timestamp = ?";
        rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
        int entity_id = 0;
        while (rowSet.next()) {
            entity_id = rowSet.getInt("entity_id");
        }
        sqlQuery =
            "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                " values(?,?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery
            , userId
            , timestamp
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
        String sqlQuery = "select like_id from likes where film_id = ? and user_id = ?";
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
                , timestamp
                , 0
                , like_id
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp
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
        String sqlQuery = "select like_id from likes where film_id = ? and user_id = ?";
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
                , timestamp
                , 0
                , like_id
                , 0);
            sqlQuery = "select entity_id from entities where timestamp = ?";
            rowSet = jdbcTemplate.queryForRowSet(sqlQuery, timestamp);
            int entity_id = 0;
            while (rowSet.next()) {
                entity_id = rowSet.getInt("entity_id");
            }
            sqlQuery =
                "insert into feeds(user_id, timestamp,eventType, operation, entity_id)" +
                    " values(?,?, ?, ?,?)";
            jdbcTemplate.update(sqlQuery
                , userId
                , timestamp
                , "LIKE"
                , "UPDATE"
                , entity_id);
            return true;
        }
        return false;
    }
}
