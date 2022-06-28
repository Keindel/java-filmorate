package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    @Qualifier("filmDbStorage")
    private final FilmDbStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserDbStorage userStorage;

    public Review createReview(Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        int defaultUseful = 0;
        validateReview(review);
        review.setUseful(defaultUseful);
        return reviewStorage.create(review);
    }

    public void updateReview(Review review) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException {
        validateReview(review);
        calculateUseful(review);
        reviewStorage.update(review);
    }


    public Review removeReviewById(Long id) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        validateReviewId(id);
        Review review = reviewStorage.getById(id);
        reviewStorage.deleteById(id);
        return review;
    }


    public Review getReviewById(Long id) throws ReviewNotFoundException {
        validateReviewId(id);
        return reviewStorage.getById(id);
    }

    public List<Review> getReviewByFilmId(Long filmId, Long count) {
        return reviewStorage.getReviewByFilmId(filmId, count)
                .stream()
                .sorted((o1, o2) -> o2.getUseful()- o1.getUseful())
                .collect(Collectors.toList());
    }

    public Collection<Review> findAll() {
        return reviewStorage.findAll();
    }


    public void addLikeFromUser(Long id, Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        Review review = reviewStorage.getById(id);
        review.getLikes().add(userId);
        updateReview(review);
    }


    public void addDislikeFromUser(Long id, Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        Review review = reviewStorage.getById(id);
        review.getDislikes().add(userId);
        updateReview(review);
    }

    public void deleteLikeFromUser(Long id, Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        Review review = reviewStorage.getById(id);
        review.getLikes().remove(userId);
        updateReview(review);
    }

    public void deleteDislikeFromUser(Long id, Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        Review review = reviewStorage.getById(id);
        review.getDislikes().remove(userId);
        updateReview(review);
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
        if (id <= 0 || !reviewStorage.findAllIds().contains(id)) {
            log.warn("review with ID not found");
            throw new ReviewNotFoundException();
        }
    }

    private void calculateUseful(Review review) {
        int defaultUseful = 0;
        if (Objects.nonNull(review.getLikes()) && !review.getLikes().isEmpty()) {
            review.setUseful(defaultUseful + review.getLikes().size());
        } else if (Objects.nonNull(review.getDislikes()) && !review.getDislikes().isEmpty()) {
            review.setUseful(defaultUseful - review.getDislikes().size());
        } else {
            review.setUseful(defaultUseful);
        }
    }
}
