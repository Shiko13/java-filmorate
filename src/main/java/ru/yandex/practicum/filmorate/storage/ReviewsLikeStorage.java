package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface ReviewsLikeStorage {
    void addLike(long reviewID, long userID);

    void addDislike(long reviewID, long userID);

    void removeLike(long reviewID, long userID);

    void removeDislike(long reviewID, long userID);

    Like getLikeById(long reviewID, long userID);

    Like getDislikeById(long reviewID, long userID);
}
