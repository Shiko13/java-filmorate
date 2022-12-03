package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void remove(long reviewId);

    Review getByID(long reviewId);

    List<Review> getAllByFilmID(long filmId, int count);

    List<Review> getAll(int count);

    void increaseRating(long reviewId);

    void reduceRating(long reviewId);
}
