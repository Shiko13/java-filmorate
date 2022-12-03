package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface ReviewsLikeStorage {
    boolean addLike(long reviewID, long userID);

    boolean addDislike(long reviewID, long userID);

    boolean removeLike(long reviewID, long userID);

    boolean removeDislike(long reviewID, long userID);

    Like getLikeById(long reviewID, long userID);

    Like getDislikeById(long reviewID, long userID);
}
