package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    @Qualifier("filmDbStorage")
    private final FilmDbStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserDbStorage userStorage;

    public Review createReview(Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        validateReview(review);
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException {
        validateReview(review);
        reviewStorage.updateReview(review);
        return reviewStorage.updateReview(review);
    }

    public Review removeReviewById(Long id) {
        return reviewStorage.removeReviewById(id);
    }


    public Review getReviewById(Long id) throws ReviewNotFoundException {
        validateReviewId(id);
        return reviewStorage.getReviewById(id);
    }


    public List<Review> getAllReviews(Long filmId, Integer count) {
        return reviewStorage.getAllReviews(filmId, count);
    }


    public Review addLikeFromUser(Long id, Long userId) {
        return reviewStorage.addLikeFromUser(id, userId);
    }


    public Review addDislikeFromUser(Long id, Long userId) {
        return reviewStorage.addDislikeFromUser(id, userId);
    }


    public Review removeLikeFromUser(Long id, Long userId) {
        return reviewStorage.removeLikeFromUser(id, userId);
    }


    public Review removeDislikeFromUser(Long id, Long userId) {
        return reviewStorage.removeDislikeFromUser(id, userId);
    }

    private void validateReview(Review review) throws ReviewNotFoundException, FilmNotFoundException, UserNotFoundException {
        if (review.getId() != null) {
            validateReviewId(review.getId());
        }
        if (filmStorage.getById(review.getFilmId()) == null) {
            log.warn("film with ID not found");
            throw new FilmNotFoundException();
        }
        if (userStorage.getById(review.getUserId()) == null) {
            log.warn("user with ID not found");
            throw new UserNotFoundException();

        }
    }

    private void validateReviewId(Long id) throws ReviewNotFoundException {
        if (id <= 0) {
            log.warn("review with ID not found");
            throw new ReviewNotFoundException();
        }
    }
}
