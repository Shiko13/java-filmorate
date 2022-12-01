package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

public interface FilmService extends Crud<Film> {
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
    Set<Film> getTopMostLiked(int count);
    List<Genre> getAllGenres();
    Genre getGenreById(long genre_id);
    List<Mpa> getMpaRatings();
    Mpa getMpaRatingById(int mpaRatingId);
}
