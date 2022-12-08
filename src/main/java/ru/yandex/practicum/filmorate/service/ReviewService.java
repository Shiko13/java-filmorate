package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review create(Review review);

    Review update(Review review);

    void remove(long reviewId);

    Review getByID(long reviewId);

    List<Review> getAllByFilmID(Long filmId, int count);
}
