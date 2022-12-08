package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface ReviewsLikeStorage {
    boolean add(long reviewId, long userId, boolean isPositive);

    boolean remove(long reviewId, long userId, boolean isPositive);

    Like get(long reviewId, long userId, boolean isPositive);
}
