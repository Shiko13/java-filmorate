package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public void deleteById(long id) {
        films.remove(id);
    }

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public boolean isExist(long id) {
        return films.containsKey(id);
    }

    @Override
    public Set<Film> showTopMostLikedFilms(int count) {
        Set<Film> mostLiked = new TreeSet<>((f1, f2) -> {
            if (f1.getLikes().size() < f2.getLikes().size()) {
                return 1;
            } else {
                return -1;
            }
        });
        mostLiked.addAll(films.values());
        return mostLiked.stream()
                .limit(count)
                .collect(Collectors.toSet());
    }
}
