package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reviews").usingGeneratedKeyColumns("review_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.isPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("useful", review.getUseful());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        review.setId(num.longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlUpdateReview = "UPDATE REVIEWS SET" +
                "                    REVIEW_ID = ?," +
                "                    CONTENT = ?," +
                "                    IS_POSITIVE = ?," +
                "                    USER_ID = ?," +
                "                    FILM_ID = ?," +
                "                    USEFUL = ?," +
                "                    VALUES(?, ?, ?, ?, ?, ?)";
        removeLikes();
        removeDislikes();
        jdbcTemplate.update(sqlUpdateReview,
                review.getId(),
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful());
        if (Objects.nonNull(review.getLikes())) {
            saveLikes(review);
        }
        if (Objects.nonNull(review.getDislikes())) {
            saveDislikes(review);
        }
        return review;
    }

    @Override
    public Review removeReviewById(Long id) {
        String sqlRemoveReviewById = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        int removeQuery = jdbcTemplate.update(sqlRemoveReviewById);
        return null;
    }

    @Override
    public Review getReviewById(Long id) {
        String sqlGetReviewById = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForObject(sqlGetReviewById, this::mapRow, id);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Integer count) {
        String sqlGetAllReviews = "SELECT * FROM REVIEWS";
        return jdbcTemplate.query(sqlGetAllReviews, this::mapRow);
    }


    private void saveLikes(Review review) {
        String sqlSaveLikes = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
        review.getLikes().forEach(id -> jdbcTemplate.update(
                sqlSaveLikes, review.getId(), id));
    }

    private void saveDislikes(Review review) {
        String sqlSaveDislikes = "INSERT INTO REVIEW_DISLIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
        review.getLikes().forEach(id -> jdbcTemplate.update(sqlSaveDislikes, review.getId(), id));
    }

    private Set<Long> getLikes(ResultSet rs) {
        String sqlGetLikes = "SELECT * FROM REVIEW_LIKES";
        return new HashSet<>(jdbcTemplate.query(sqlGetLikes, (rs1, rowNum)
                -> rs1.getLong("user_id")));
    }

    private Set<Long> getDislikes(ResultSet rs) {
        String sqlGetDislikes = "SELECT * FROM REVIEW_DISLIKES";
        return new HashSet<>(jdbcTemplate.query(sqlGetDislikes, (rs1, rowNum)
                -> rs1.getLong("user_id")));
    }

    private void removeLikes() {
        String sqlRemoveLikes = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlRemoveLikes);
    }
    private void removeDislikes() {
        String sqlRemoveDislikes = "DELETE FROM REVIEW_DISLIKES WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlRemoveDislikes);
    }


    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getInt("useful"),
                getLikes(rs),
                getDislikes(rs));
    }
}


