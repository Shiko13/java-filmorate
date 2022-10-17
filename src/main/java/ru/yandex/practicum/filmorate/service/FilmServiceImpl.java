package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    public List<Film> getAll() {
        log.debug("Start request GET to /films)");

        return filmStorage.findAll();
    }

    @Override
    public Film getById(long id) {
        log.debug("Start request GET to /films/{id}, id = " + id);

        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Film with id = " + id + " not found"));
    }

    @Override
    public Set<Film> showTopMostLikedFilms(int count) {
        log.debug("Start request GET to /films/popular, count = " + count);

        return filmStorage.showTopMostLikedFilms(count);
    }

    @Override
    public Film create(Film film) {
        log.debug("Start request POST to /films");
        if (filmStorage.isExist(film.getId())) {
            log.warn("Unsuccessful request POST to /films, attempt to add existing film");
            throw new ValidateException("Filmorate already contains this film");
        }
        throwIfNotValid(film);

        film.setId(id++);
        log.info("Process of adding new film");
        filmStorage.create(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Start request GET to /films/{id}, id = " + id);
        filmStorage.findById(film.getId())
                .orElseThrow(() ->
                        new NotFoundException("Film with id = " + film.getId() + " not found"));
        throwIfNotValid(film);

        log.info("Process of updating the film");
        filmStorage.update(film);

        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.debug("Start request PUT to /films/{id}/like/{userId}, id = " + id + " ,userId = " + userId);
        filmStorage.findById(filmId)
                .orElseThrow(() ->
                        new NotFoundException("Film with id = " + filmId + " not found")).addLike(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.debug("Start request DELETE to /films/{id}/like/{userId}, id = " + id + ", userId = " + userId);

        Film film = filmStorage.findById(filmId)
                .orElseThrow(() ->
                        new NotFoundException("Film with id = " + filmId + " not found"));

        if (!film.isContainsLike(userId)) {
            log.warn("Unsuccessful request DELETE to /films/{id}/like/{userId}, this user haven't likes");
            throw new NotFoundException("Not found likes from user with id = " + userId +
                    " to film with id = " + filmId);
        }
        film.deleteLike(userId);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /films/{id}, id = " + id);
        filmStorage.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /films");
        filmStorage.deleteAll();
    }

    @Override
    public void throwIfNotValid(Film film) {
        log.debug("Start validation of film");

        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("Lumiere brothers look at you with surprise! (to much early date)");
        }

        log.debug("Validation successful passed");
    }
}