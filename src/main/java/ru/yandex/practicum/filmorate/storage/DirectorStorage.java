package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director create(Director director);
    Director update(Director director);
    void deleteAll();
    void deleteById(long directorId);
    List<Director> readAll();
    Director readById(long directorId);
    List<Film> set(List<Film> films);
}
