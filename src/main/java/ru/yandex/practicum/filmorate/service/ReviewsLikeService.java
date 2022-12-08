package ru.yandex.practicum.filmorate.service;

public interface ReviewsLikeService {
    void add(long reviewId, long userId, boolean isPositive);

    void remove(long reviewId, long userId, boolean isPositive);
}
