package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film findFilmById(long id);

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Film changeFilm(Film film);

    void deleteFilmById(long id);

    void deleteAllFilms();
    boolean isContainFilm(long id);
}
