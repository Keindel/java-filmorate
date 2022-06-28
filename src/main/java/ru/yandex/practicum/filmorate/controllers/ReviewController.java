package ru.yandex.practicum.filmorate.controllers;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public Review createReview(@Valid @RequestBody @NonNull Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        return reviewService.createReview(review);
    }


    @PutMapping()
    public Review updateReview(@Valid @RequestBody @NonNull Review review) throws ReviewNotFoundException,
            UserNotFoundException,
            FilmNotFoundException {
        reviewService.updateReview(review);
        return review;
    }


    @DeleteMapping("/{reviewId}")
    public Review deleteReviewById(@PathVariable Long reviewId) throws ReviewNotFoundException, UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return reviewService.removeReviewById(reviewId);
    }


    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        return reviewService.getReviewById(reviewId);
    }


    @GetMapping()
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                     @RequestParam(required = false) Long count) {
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
                                     @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        reviewService.deleteLikeFromUser(reviewId, userId);
    }


    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromUser(@PathVariable Long reviewId,
                                        @PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        reviewService.deleteDislikeFromUser(reviewId, userId);
    }
}

