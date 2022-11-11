package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film findFilmById(long id);
    List<Film> findAllFilms();
    Film createFilm(Film film);
    void updateFilm(Film film);
    void deleteFilmById(long id);
    void deleteAllFilms();
    Set<Film> readTopMostLikedFilms(int count);
}
