package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearchBy;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.TypeOfEvent;
import ru.yandex.practicum.filmorate.model.TypeOfOperation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final DirectorStorage directorStorage;
    private final UserEventListStorage userEventListStorage;

    @Override
    public List<Film> getAll() {
        log.debug("Start request GET to /films");

        List<Film> films = filmStorage.findAll();
        genreStorage.set(films);
        directorStorage.set(films);
        return films;
    }

    @Override
    public Film getById(long id) {
        log.debug("Start request GET to /films/{}", id);

        Film film = filmStorage.findById(id);
        List<Film> films = genreStorage.set(List.of(film));
        directorStorage.set(films);
        return films.get(0);
    }

    @Override
    public Set<Film> getTopPopular(Long genreId, Integer releaseYear, int count) {
        log.debug("GET /films/popular?count={}&genreId={}&year={}", count, genreId, releaseYear);

        List<Film> popularFilms = new ArrayList<>(filmStorage.getTopPopular(genreId, releaseYear, count));
        genreStorage.set(popularFilms);
        directorStorage.set(popularFilms);

        return new HashSet<>(popularFilms);
    }

    @Override
    public Set<Film> getCommon(long userId, long friendId) {
        log.debug("Start request GET /films/common?userId={}&friendId={}", userId, friendId);
        return filmStorage.getCommon(userId, friendId);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Start request GET to /films");

        return genreStorage.readAll();
    }

    @Override
    public Genre getGenreById(long genre_id) {
        log.debug("Start request GET to /genres/{}", genre_id);

        validateId(genre_id);
        return genreStorage.readById(genre_id);
    }

    @Override
    public List<Mpa> getMpaRatings() {
        log.debug("Start request GET to /mpa");

        return mpaRatingStorage.readAll();
    }

    @Override
    public Mpa getMpaRatingById(int mpaRatingId) {
        log.debug("Start request GET to /mpa/{}", mpaRatingId);

        if (mpaRatingId < 0) {
            throw new NotFoundException("Mpa should be positive");
        }

        return mpaRatingStorage.readById(mpaRatingId);
    }

    @Override
    public List<Film> getSortListByDirector(long directorId, FilmSortBy filmSortBy) {
        List<Film> films;

        switch (filmSortBy) {
            case YEAR:
                films = filmStorage.getSortByYearFromDirector(directorId);
                break;
            case LIKES:
                films = filmStorage.getSortByLikesFromDirector(directorId);
                break;
            default:
                throw new ValidateException("Incorrect parameters of request");
        }

        genreStorage.set(films);
        directorStorage.set(films);

        return films;
    }

    @Override
    public List<Film> searchFilmsByTitleByDirector(String query, List<FilmSearchBy> by) {
        log.debug("Start request GET to /films/search query = {}, by = {}", query, by);
        List<Film> films;
        if (by.contains(FilmSearchBy.title) && by.contains(FilmSearchBy.director)) {
            log.info("Запрошен поиск фильмов по {} среди названий и режисеров", query);
            films = filmStorage.searchFilmsByTitleAndDirector(query);
        } else if (by.contains(FilmSearchBy.title) && !by.contains(FilmSearchBy.director)) {
            log.info("Запрошен поиск фильмов по {} среди названий", query);
            films = filmStorage.searchFilmsByTitle(query);
        } else if (!by.contains(FilmSearchBy.title) && by.contains(FilmSearchBy.director)) {
            log.info("Запрошен поиск фильмов по {} среди режисеров", query);
            films = filmStorage.searchFilmsByDirector(query);
        } else {
            throw new ValidateException("Incorrect parameters of request");
        }
        genreStorage.set(films);
        directorStorage.set(films);

        return films;
    }

    @Override
    public Film create(Film film) {
        log.debug("Start request POST to /films, with id = {}, name = {}, description = {}, " +
                        "releaseDate = {}, duration = {}, mpa = {}, genres = {}, directors = {}",
                film.getId(), film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa(),
                film.getGenres(), film.getDirectors());

        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        log.debug("Start request GET to /films/{}", film.getId());

        filmStorage.update(film);

        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.debug("Start request PUT to /films/{}/like/{}", filmId, userId);

        likeStorage.create(filmId, userId);

        userEventListStorage.addEvent(userId, String.valueOf(TypeOfEvent.LIKE), String.valueOf(TypeOfOperation.ADD),
                filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.debug("Start request DELETE to /films/{}/like/{}", filmId, userId);

        likeStorage.delete(filmId, userId);

        userEventListStorage.addEvent(userId, String.valueOf(TypeOfEvent.LIKE), String.valueOf(TypeOfOperation.REMOVE),
                filmId);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /films/{}", id);

        filmStorage.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /films");

        filmStorage.deleteAll();
    }

    private static void validateId(long id) {
        log.debug("Start validation of id");

        if (id <= 0) {
            throw new NotFoundException(String.format("Object with id = %d is not found.", id));
        }

        log.debug("Validation of id successful passed");
    }
}
