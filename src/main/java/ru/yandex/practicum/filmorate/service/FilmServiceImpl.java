package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoLikeException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private long id = 1;
    private final LocalDate BIRTHDAY_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.trace("Start request GET to /films)");
        return filmStorage.findAllFilms();
    }

    @Override
    public Film findFilmById(long id) {
        log.trace("Start request GET to /films/{id}");
        if (!filmStorage.isContainFilm(id)) {
            log.error("Unsuccessful request GET to /films/{id}");
            throw new NoSuchFilmException("Film with id = " + id + " not found");
        }
        return filmStorage.findFilmById(id);
    }

    @Override
    public Collection<Film> showTopMostLikedFilms(int count) {
        log.trace("Start request GET to /films/popular");
        Set<Film> mostLiked = new TreeSet<>((f1, f2) -> {
            if (f1.getLikes().size() < f2.getLikes().size()) {
                return 1;
            } else {
                return -1;
            }
        });
        mostLiked.addAll(filmStorage.findAllFilms());
        return mostLiked.stream()
                .limit(count)
                .collect(Collectors.toSet());
    }

    @Override
    public Film createFilm(Film film) {
        log.trace("Start request POST to /films");
        if (!filmStorage.isContainFilm(film.getId())) {
            validationFilm(film);
        } else {
            log.error("Unsuccessful request POST to /films");
            throw new ValidateException("Filmorate already contains this film");
        }
        film.setId(id++);
        log.info("Process of adding new film");
        filmStorage.createFilm(film);
        return film;
    }

    @Override
    public Film changeFilm(Film film) {
        log.trace("Start request GET to /films/{id}");
        if (!filmStorage.isContainFilm(film.getId())) {
            log.error("Unsuccessful request POST to /films/{id}");
            throw new NoSuchFilmException("Film with id = " + film.getId() + " not found");
        }
        validationFilm(film);
        log.info("Process of updating the film");
        filmStorage.changeFilm(film);
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.trace("Start request PUT to /films/{id}/like/{userId}");
        filmStorage.findFilmById(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.trace("Start request DELETE to /films/{id}/like/{userId}");
        if (!filmStorage.isContainFilm(filmId)) {
            log.error("Unsuccessful request DELETE to /films/{id}/like/{userId}");
            throw new NoSuchFilmException("Film with id = " + filmId + " not found");
        }
        if (!filmStorage.findFilmById(filmId).isContainsLike(userId)) {
            log.error("Unsuccessful request DELETE to /films/{id}/like/{userId}");
            throw new NoLikeException("Not found likes from user with id = " + userId +
                    " to film with id = " + filmId);
        }
        filmStorage.findFilmById(filmId).deleteLike(userId);
    }

    @Override
    public void deleteFilmById(long id) {
        log.trace("Start request DELETE to /films/{id}");
        filmStorage.deleteFilmById(id);
    }

    @Override
    public void deleteAllFilms() {
        log.trace("Start request DELETE to /films");
        filmStorage.deleteAllFilms();
    }

    @Override
    public void validationFilm(Film film) {
        log.trace("Start validation of film");
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("To much early date");
        }
        log.trace("Validation successful passed");
    }
}
