package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final FeedService feedService;

    @PostMapping
    public Review createReview(@Valid @RequestBody @NonNull Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        Review review1 = reviewService.createReview(review);
        feedService.addReview(review);
        return review1;
    }


    @PutMapping()
    public Review updateReview(@Valid @RequestBody @NonNull Review review) throws ReviewNotFoundException,
        UserNotFoundException,
        FilmNotFoundException {
        reviewService.updateReview(review);
        feedService.updateReview(review);
        return review;
    }


    @DeleteMapping("/{reviewId}")
    public Review deleteReviewById(@PathVariable Long reviewId) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        feedService.deleteReview(reviewId);
        return reviewService.removeReviewById(reviewId);
    }


    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        return reviewService.getReviewById(reviewId);
    }


    @GetMapping()
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") Long count) {
        return reviewService.getReviewByFilmId(filmId, count);
    }


    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeFromUser(@PathVariable Long reviewId,
                                @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        reviewService.addLikeFromUser(reviewId, userId);
    }


    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeFromUser(@PathVariable Long reviewId,
                                   @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        reviewService.addDislikeFromUser(reviewId, userId);
    }


    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromUser(@PathVariable Long reviewId,
                                   @PathVariable Long userId) {
        reviewService.deleteLikeFromUser(reviewId, userId);
    }


    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromUser(@PathVariable Long reviewId,
                                      @PathVariable Long userId)  {
        reviewService.deleteDislikeFromUser(reviewId, userId);
    }
}

