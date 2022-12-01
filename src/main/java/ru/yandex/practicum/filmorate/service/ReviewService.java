package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review create(Review review);

    Review update(Review review);

    void remove(long reviewID);

    Review getByID(long reviewID);

    List<Review> getAllByFilmID(long reviewID);

    void addLike(long reviewID, long userID);

    void addDislike(long reviewID, long userID);

    void removeLike(long reviewID, long userID);

    void removeDislike(long reviewID, long userID);
}
