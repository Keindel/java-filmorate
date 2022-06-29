package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reviews").usingGeneratedKeyColumns("review_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("useful", review.getUseful());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        review.setId(num.longValue());
        log.info("Review {} saved successfully", review.getContent());
        return review;
    }

    @Override
    public void update(Review review) {
        String sqlUpdateReview = "UPDATE reviews SET" +
                "                    content = ?," +
                "                    is_positive = ?" +
                "                    WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateReview, review.getContent(), review.getIsPositive(), review.getId());
        log.info("Review {} successfully updated", review.getContent());
    }

    @Override
    public void deleteById(Long id) {
        String sqlRemoveReviewById = "DELETE FROM reviews WHERE review_id = ?";
        Review review = getById(id);
        removeLikes(review);
        removeDislikes(review);
        jdbcTemplate.update(sqlRemoveReviewById, id);
        log.info("Review {} successfully deleted", review.getContent());
    }

    @Override
    public Review getById(Long id) {
        String sqlGetReviewById = "SELECT * FROM reviews WHERE review_id = ?";
        log.info("Review {} successfully received", id);
        return jdbcTemplate.queryForObject(sqlGetReviewById, this::mapRow, id);
    }

    @Override
    public Collection<Review> findAll() {
        String sqlGetAllReviews = "SELECT * FROM reviews";
        log.info("All reviews successfully received");
        return jdbcTemplate.query(sqlGetAllReviews, this::mapRow);
    }

    @Override
    public long getSize() {
        String sqlGetSize = "SELECT COUNT(*) FROM reviews";
        log.info("Received total number of reviews");
        return jdbcTemplate.queryForObject(sqlGetSize, Long.class);
    }

    public List<Long> findAllIds() {
        String sqlFindAllIds = "SELECT R.review_id FROM reviews AS R";
        log.info("Got id of all reviews");
        return jdbcTemplate.query(sqlFindAllIds, (rs, rowNum) -> rs.getLong("review_id"));
    }

    public List<Review> getReviewByFilmId(Long filmId, Long count) {
        String sqlCountReviewByFilm = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
        String sqlCountAllReviewWithoutFilm = "SELECT * FROM reviews LIMIT ?";
        if (Objects.nonNull(filmId)) {
            log.info("Received {} reviews for the film {}", count, filmId);
            return jdbcTemplate.query(sqlCountReviewByFilm, this::mapRow, filmId, count);
        } else {
            log.info("Received {} reviews", count);
            return jdbcTemplate.query(sqlCountAllReviewWithoutFilm, this::mapRow, count);
        }
    }


    public void saveLikes(Review review) {
        String sqlSaveLikes = "INSERT INTO review_likes (user_id, review_id) VALUES (?, ?)";
        review.getLikes().forEach(id -> jdbcTemplate.update(sqlSaveLikes, id, review.getId()));
        updateUseful(review);
        log.info("Like saved successfully");
    }

    public void saveDislikes(Review review) {
        String sqlSaveDislikes = "INSERT INTO review_dislikes (user_id, review_id) VALUES (?, ?)";
        review.getDislikes().forEach(id -> jdbcTemplate.update(sqlSaveDislikes, id, review.getId()));
        updateUseful(review);
        log.info("Dislike saved successfully");
    }

    private Set<Long> getLikes(ResultSet rs) throws SQLException {
        String sqlGetLikes = "SELECT * FROM review_likes WHERE review_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlGetLikes, (rs1, rowNum)
                -> rs1.getLong("user_id"), rs.getLong("review_id")));
    }

    private Set<Long> getDislikes(ResultSet rs) throws SQLException {
        String sqlGetDislikes = "SELECT * FROM review_dislikes WHERE review_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlGetDislikes, (rs1, rowNum)
                -> rs1.getLong("user_id"), rs.getLong("review_id")));
    }

    public void removeLikes(Review review) {
        String sqlRemoveLikes = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlRemoveLikes, review.getId(), review.getUserId());
        updateUseful(review);
        log.info("Like successfully removed");
    }

    public void removeDislikes(Review review) {
        String sqlRemoveDislikes = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlRemoveDislikes, review.getId(), review.getUserId());
        updateUseful(review);
        log.info("Dislike successfully removed");
    }

    private void updateUseful(Review review) {
        String sqlUpdateUseful = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlUpdateUseful, review.getUseful(), review.getId());
        log.info("Utility rating updated successfully");
    }


    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Review(rs.getLong("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getInt("useful"),
                getLikes(rs),
                getDislikes(rs));
    }
}


