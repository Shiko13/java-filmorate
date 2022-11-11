package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film findFilmById(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        films.replace(film.getId(), film);
    }

    @Override
    public void deleteFilmById(long id) {
        films.remove(id);
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public Set<Film> readTopMostLikedFilms(int count) {
        return new HashSet<>();
        //Without database, it's impossible ¯\_(ツ)_/¯
    }
}
