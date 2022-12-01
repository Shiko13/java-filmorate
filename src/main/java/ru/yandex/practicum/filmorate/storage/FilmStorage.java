package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film findById(long id);
    List<Film> findAll();
    Film create(Film film);
    void update(Film film);
    void deleteById(long id);
    void deleteAll();
    Set<Film> readTopMostLiked(int count);
}
