package ru.yandex.practicum.filmorate.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RepositoryCleaner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RepositoryCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clean() {
        String sqlTruncate =
                " DELETE FROM users;" +
                        " ALTER TABLE users ALTER COLUMN user_id RESTART START WITH 1;" +
                        " ALTER TABLE users DROP CONSTRAINT IF EXISTS CONSTRAINT_4D;" +
                        " ALTER TABLE USERS ADD UNIQUE (email);" +

                        " DELETE FROM films;" +
                        " ALTER TABLE films ALTER COLUMN film_id RESTART START WITH 1;";
        jdbcTemplate.update(sqlTruncate);
    }
}
