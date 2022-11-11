package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorage {
    Like createLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
