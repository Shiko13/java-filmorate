package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaRatingStorage {
    List<Mpa> readAllMpaRatings();
    Mpa readMpaRatingById(int mpaRatingId);
}
