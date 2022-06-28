package ru.yandex.practicum.filmorate.controllers;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Добавление нового отзыва.
     */
    @PostMapping
    public Review createReview(@Valid @RequestBody @NonNull Review review) throws UserNotFoundException, FilmNotFoundException, ReviewNotFoundException {
        return reviewService.createReview(review);
    }

    /**
     * Редактирование уже имеющегося отзыва.
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody @NonNull Review review) throws ReviewNotFoundException,
            UserNotFoundException,
            FilmNotFoundException {
        return reviewService.updateReview(review);
    }

    /**
     * Удаление уже имеющегося отзыва.
     */
    @DeleteMapping("{id}")
    public Review removeReviewById(@PathVariable Long id) {
        return reviewService.removeReviewById(id);
    }

    /**
     * Получение отзыва по идентификатору.
     */
    @GetMapping("{id}")
    public Review getReviewById(@PathVariable Long id) throws ReviewNotFoundException {
        return reviewService.getReviewById(id);
    }

    /**
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то всех.
     * Если кол-во не указано то 10.
     */
    @GetMapping("{filmId}{count}")
    public List<Review> getAllReviews(@PathVariable Long filmId,
                                      @PathVariable Integer count) {
        return reviewService.getAllReviews(filmId, count);
    }

    /**
     * Пользователь ставит лайк отзыву.
     */
    @PutMapping("{id}/like/{userId}")
    public Review addLikeFromUser(@PathVariable Long id,
                                  @PathVariable Long userId) {
        return reviewService.addLikeFromUser(id, userId);
    }

    /**
     * Пользователь ставит дизлайк отзыву.
     */
    @PutMapping("{id}/dislike/{userId}")
    public Review addDislikeFromUser(@PathVariable Long id,
                                     @PathVariable Long userId) {
        return reviewService.addDislikeFromUser(id, userId);
    }

    /**
     * Пользователь удаляет лайк отзыву.
     */
    @DeleteMapping("{id}/like/{userId}")
    public Review removeLikeFromUser(@PathVariable Long id,
                                     @PathVariable Long userId) {
        return reviewService.removeLikeFromUser(id, userId);
    }

    /**
     * Пользователь удаляет дизлайк отзыву.
     */
    @DeleteMapping("{id}/dislike/{userId}")
    public Review removeDislikeFromUser(@PathVariable Long id,
                                        @PathVariable Long userId) {
        return reviewService.removeDislikeFromUser(id, userId);
    }
}

