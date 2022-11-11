package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final LocalDate BIRTHDAY_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDb") FilmStorage filmStorage, LikeStorage likeStorage,
                           GenreStorage genreStorage, MpaRatingStorage mpaRatingStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaRatingStorage = mpaRatingStorage;
    }

    @Override
    public List<Film> getAll() {
        log.debug("Start request GET to /films");

        return filmStorage.findAllFilms();
    }

    @Override
    public Film getById(long id) {
        log.debug("Start request GET to /films/{}", id);

        return filmStorage.findFilmById(id);
    }

    @Override
    public Set<Film> getTopMostLikedFilms(int count) {
        log.debug("Start request GET to /films/popular, count = " + count);

        return filmStorage.readTopMostLikedFilms(count);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Start request GET to /films");

        return genreStorage.readAllGenres();
    }

    @Override
    public Genre getGenreById(long genre_id) {
        log.debug("Start request GET to /genres/{}", genre_id);

        validateId(genre_id);
        return genreStorage.readGenreById(genre_id);
    }

    @Override
    public List<Mpa> getMpaRatings() {
        log.debug("Start request GET to /mpa");

        return mpaRatingStorage.readAllMpaRatings();
    }

    @Override
    public Mpa getMpaRatingById(int mpaRatingId) {
        log.debug("Start request GET to /mpa/{}", mpaRatingId);

        if (mpaRatingId < 0 || mpaRatingId > 5) {
            throw new NotFoundException("Mpa should be from 1 to 5");
        }

        return mpaRatingStorage.readMpaRatingById(mpaRatingId);
    }

    @Override
    public Film create(Film film) {
        log.debug("Start request POST to /films, with id = {}, name = {}, description = {}, " +
                        "releaseDate = {}, duration = {}, mpa = {}, genres = {}",
                film.getId(), film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa(),
                film.getGenres());

        throwIfNotValidDate(film);

        return filmStorage.createFilm(film);
    }

    @Override
    public Film update(Film film) {
        log.debug("Start request GET to /films/{}", film.getId());

        throwIfNotValidDate(film);
        filmStorage.updateFilm(film);

        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.debug("Start request PUT to /films/{}/like/{}", filmId, userId);

        validateId(filmId);
        validateId(userId);
        likeStorage.createLike(filmId, userId);
    }

    private static void validateId(long id) {
        log.debug("Start validation of id");

        if (id <= 0) {
            throw new NotFoundException(String.format("Object with id = %d is not found.", id));
        }

        log.debug("Validation of id successful passed");
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.debug("Start request DELETE to /films/{}/like/{}", filmId, userId);

        validateId(filmId);
        validateId(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /films/{}", id);

        filmStorage.deleteFilmById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /films");

        filmStorage.deleteAllFilms();
    }

    @Override
    public void throwIfNotValidDate(Film film) {
        log.debug("Start validation of film");

        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("Lumiere brothers look at you with surprise! (to much early date)");
        }

        log.debug("Validation successful passed");
    }
}