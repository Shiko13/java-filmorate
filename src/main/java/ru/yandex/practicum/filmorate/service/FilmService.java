package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmService extends Crud<Film> {
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
    List<Genre> getAllGenres();
    Genre getGenreById(long genre_id);
    List<Mpa> getMpaRatings();
    Mpa getMpaRatingById(int mpaRatingId);
    List<Film> getSortListByDirector(long directorId, FilmSortBy filmSortBy);
    List<Film> searchFilmsByTitleByDirector(String query, String by);
    Set<Film> getTopPopular(Long genreId, Integer releaseYear, int count);
    Set<Film> getCommon(long userId, long friendId);
}
