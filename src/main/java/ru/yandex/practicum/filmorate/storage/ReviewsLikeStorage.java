package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface ReviewsLikeStorage {
    boolean addLike(long reviewId, long userId);

    boolean addDislike(long reviewId, long userId);

    boolean removeLike(long reviewId, long userId);

    boolean removeDislike(long reviewId, long userId);

    Like getLikeById(long reviewId, long userId);

    Like getDislikeById(long reviewId, long userId);
}
