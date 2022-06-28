package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage{
    Review createReview(Review review);
    Review updateReview(Review review) throws ReviewNotFoundException;
    Review removeReviewById(Long id);
    Review getReviewById(Long id);
    List<Review> getAllReviews(Long filmId, Integer count);

}
