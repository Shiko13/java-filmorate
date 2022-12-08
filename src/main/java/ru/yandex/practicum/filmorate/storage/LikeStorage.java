package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    Like create(long filmId, long userId);
    void delete(long filmId, long userId);
    List<Long> getRecommendations(long userId);
}
