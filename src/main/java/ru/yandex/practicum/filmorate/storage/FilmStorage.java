package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film findById(long id);
    List<Film> findAll();
    Film create(Film film);
    void update(Film film);
    void deleteById(long id);
    void deleteAll();
    List<Film> getSortByYearFromDirector(long directorId);
    List<Film> getSortByLikesFromDirector(long directorId);
    List<Film> searchFilmsByTitleByDirector(String query, String by);
    Set<Film> getTopPopular(Long genreId, Integer releaseYear, int count);
    Collection<Film> getCommon(long userId, long friendId);
}
