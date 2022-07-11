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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    private final FeedService feedService;
    @Qualifier("filmDbStorage")
    private final FilmDbStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserDbStorage userStorage;

    public Review createReview(Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException, DirectorNotFoundException {
        int defaultUseful = 0;
        validateReview(review);
        review.setUseful(defaultUseful);
        Review updateReview = reviewStorage.create(review);
        feedService.addReview(review);
        return updateReview;
    }

    public void updateReview(Review review) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        validateReview(review);
        reviewStorage.update(review);
        feedService.updateReview(review);
    }


    public Review removeReviewById(Long id) throws ReviewNotFoundException{
        validateReviewId(id);
        Review review = reviewStorage.getById(id);
        feedService.deleteReview(id);
        reviewStorage.deleteById(id);
        return review;
    }


    public Review getReviewById(Long id) throws ReviewNotFoundException {
        validateReviewId(id);
        return reviewStorage.getById(id);
    }

    public List<Review> getReviewByFilmId(Long filmId, Long count) {
        return reviewStorage.getReviewByFilmId(filmId, count);
    }

    public Collection<Review> findAll() {
        return reviewStorage.findAll();
    }


    public void addLikeFromUser(Long id, Long userId)  {
        Review review = reviewStorage.getById(id);
        review.getLikes().add(userId);
        calculateUseful(review);
        reviewStorage.saveLikes(review);
    }


    public void addDislikeFromUser(Long id, Long userId)  {
        Review review = reviewStorage.getById(id);
        review.getDislikes().add(userId);
        calculateUseful(review);
        reviewStorage.saveDislikes(review);
    }

    public void deleteLikeFromUser(Long id, Long userId){
        Review review = reviewStorage.getById(id);
        review.getLikes().remove(userId);
        calculateUseful(review);
        reviewStorage.removeLikes(review);
    }

    public void deleteDislikeFromUser(Long id, Long userId)  {
        Review review = reviewStorage.getById(id);
        review.getDislikes().remove(userId);
        calculateUseful(review);
        reviewStorage.removeDislikes(review);
    }


    private void validateReview(Review review) throws ReviewNotFoundException, FilmNotFoundException, UserNotFoundException, DirectorNotFoundException {
        if (review.getReviewId() != null) {
            validateReviewId(review.getReviewId());
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
        if (!reviewStorage.findReviewId(id)) {
            log.warn("review with ID not found");
            throw new ReviewNotFoundException();
        }
    }

    private void calculateUseful(Review review) {
        int defaultUseful = reviewStorage.getById(review.getReviewId()).getUseful();
        if (Objects.nonNull(review.getLikes()) && !review.getLikes().isEmpty()) {
            review.setUseful(defaultUseful + review.getLikes().size());
        } else if (Objects.nonNull(review.getDislikes()) && !review.getDislikes().isEmpty()) {
            review.setUseful(defaultUseful - review.getDislikes().size());
        } else {
            review.setUseful(defaultUseful);
        }
    }
}

