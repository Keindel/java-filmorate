package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


/*
* Эти классы будут DAO — объектами доступа к данным.
Напишите в DAO соответствующие мапперы и методы,
*  позволяющие сохранять пользователей и фильмы в базу данных и получать их из неё.
* */

@Component
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
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        // todo
        // (! rs.next()) здесь лучше?
        // нужно ли вообще здесь выбрасывать исключение? сработает валидация при попытке создать пользователя
        //if (rs.getLong("user_id") == 0) throw new UserNotFoundException();
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
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
                "values (DEFAULT, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday());
        return User.builder()
                .
                .build();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
