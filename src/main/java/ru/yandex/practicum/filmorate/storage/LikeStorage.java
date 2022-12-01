package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorage {
    Like create(long filmId, long userId);
    void delete(long filmId, long userId);
}
