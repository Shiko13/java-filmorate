package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Optional<Film> findById(long id);
    List<Film> findAll();
    Film create(Film film);
    Film update(Film film);
    void deleteById(long id);
    void deleteAll();
    boolean isExist(long id);
    Set<Film> showTopMostLikedFilms(int count);
}
