package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getSize() {
        String sqlQuery = "select COUNT(*) from users";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class);
    }

    @Override
    public User getById(Long id) throws UserNotFoundException {
        String sqlQuery = "select user_id, email, login, name, birthday" +
                " from users where user_id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);

        String sqlFriends = "select friends.friend_id, fs.status_name" +
                " from friends" +
                " left join friendship_status as fs on friends.FRIENDSHIP_STATUS_ID = fs.STATUS_NAME " +
                " where friends.user_id = ?";
        Map<Long, FriendshipStatus> friendsMap
                = jdbcTemplate.query(sqlFriends, this::mapRowToFriendIdAndStatus, id)
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        user.setFriends(friendsMap);

        return user;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
//         if (! rs.next()) throw new UserNotFoundException();
        // нужно ли вообще здесь выбрасывать исключение? сработает валидация при попытке создать пользователя
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Map.Entry<Long, FriendshipStatus> mapRowToFriendIdAndStatus(ResultSet rs, int rowNum) throws SQLException {
        return new AbstractMap.SimpleEntry<>(rs.getLong("friend_id")
                , FriendshipStatus.valueOf(rs.getString("status_id")));
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "select user_id, email, login, name, birthday" +
                " from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into users (user_id, email, login, name, birthday)" +
                " values (DEFAULT, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        long idFromDb = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(idFromDb);
        return user;
    }

    @Override
    public void update(User user) {
        //TODO if user does not exist then SQLException?
        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ?" +
                " where user_id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , Date.valueOf(user.getBirthday())
                , user.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void requestFriendship(Long userId, Long friendToAddId) {
        String sqlQuery = "insert into friends(user_id, friend_id, friendship_status_id)" +
                " values(?, ?, ?)";
        jdbcTemplate.update(sqlQuery
                , userId
                , friendToAddId
                , 1);
    }

    public void deleteFriendFromUser(Long userId, Long friendToDellId) {
        String sqlQuery = "delete from friends" +
                " where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery
                , userId
                , friendToDellId);
    }
}
