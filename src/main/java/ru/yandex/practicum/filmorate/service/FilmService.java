package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface FilmService extends Crud<Film> {
    void validation(Film film);
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
    Set<Film> showTopMostLikedFilms(int count);
}
