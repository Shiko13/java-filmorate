package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Collection<Film> findAllFilms();
    Film findFilmById(long id);
    Film createFilm(Film film);
    Film changeFilm(Film film);
    void deleteFilmById(long id);
    void deleteAllFilms();
    void validationFilm(Film film);
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
    Collection<Film> showTopMostLikedFilms(int count);
}
