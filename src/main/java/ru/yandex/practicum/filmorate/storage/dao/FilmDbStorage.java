package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDb")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film findFilmById(long filmId) {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "mr.MPA_RATING_ID, mr.MPA_RATING_NAME\n" +
                "from films as f\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n" +
                "where f.FILM_ID = ?";

        Film film;
        try {
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
            film = mapRowToFilm(sqlRowSet);
        } catch (Exception e) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }

        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "mr.MPA_RATING_ID, mr.MPA_RATING_NAME, g.GENRE_ID, g.GENRE_NAME\n" +
                "from films as f\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n" +
                "join film_genres as fg on f.FILM_ID = fg.FILM_ID\n" +
                "left outer join GENRES as g on fg.GENRE_ID = g.GENRE_ID\n";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> films = mapRowToFilmSet(sqlRowSet);

        for (Film film : films) {
            setGenreFilm(film);
        }

        return new ArrayList<>(films);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "insert into films(film_name, description, release_date, duration, MPA_RATING) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        createGenres(film);

        return film;
    }

    private void createGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }

        List<Genre> genres = film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList());
        for (Genre genre : genres) {
            String sqlQueryGenre = "insert into film_genres values (?, ?)";
            jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
        }
        film.setGenres(genres);
    }

    private void updateGenres(Film film) {
        String sqlQuery = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        createGenres(film);
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "update films set film_name = ?, description = ?, release_date = ?, duration = ?, " +
                "MPA_RATING = ? where film_id = ?";

        findFilmById(film.getId());

        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
    }

    @Override
    public void deleteFilmById(long filmId) {
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void deleteAllFilms() {
        String sqlQuery = "delete from films";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Set<Film> readTopMostLikedFilms(int count) {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, mr.MPA_RATING_ID, " +
                "mr.MPA_RATING_NAME\n" +
                "from FILMS as f\n" +
                "left join LIKES as l on f.FILM_ID = l.FILM_ID\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n" +
                "group by f.film_id\n" +
                "order by count(l.FILM_ID) desc\n" +
                "limit " + count;

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> popularFilms = mapRowToFilmSet(sqlRowSet);

        for (Film film : popularFilms) {
            setGenreFilm(film);
        }

        return popularFilms;
    }

    private Film mapRowToFilm(SqlRowSet sqlRowSet) {
        sqlRowSet.next();
        Film film;
        try {
            film = Film.builder().
                    id(sqlRowSet.getInt("film_id")).
                    name(sqlRowSet.getString("film_name")).
                    description(sqlRowSet.getString("description")).
                    releaseDate(Objects.requireNonNull(sqlRowSet.getDate("release_date")).toLocalDate()).
                    duration(sqlRowSet.getInt("duration")).
                    mpa(Mpa.builder().
                            id(sqlRowSet.getInt("mpa_rating_id")).
                            name(sqlRowSet.getString("mpa_rating_name")).
                            build()).
                    build();
        } catch (Exception e) {
            throw new NotFoundException("Film with those parameters not found");
        }

        setGenreFilm(film);
        return film;
    }

    private void setGenreFilm(Film film) {
        String sqlQueryGenre = "select * from film_genres as fg\n" +
                "join genres as g on g.genre_id = fg.genre_id\n" +
                "where fg.film_id = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQueryGenre, film.getId());
        Set<Genre> genres = new LinkedHashSet<>();

        while (sqlRowSet.next()) {
            Genre genre = Genre.builder().
                    id(sqlRowSet.getInt("genre_id")).
                    name(sqlRowSet.getString("genre_name")).
                    build();
            genres.add(genre);
        }

        film.setGenres(new ArrayList<>(genres));
    }

    private Set<Film> mapRowToFilmSet(SqlRowSet sqlRowSet) {
        Set<Film> films = new LinkedHashSet<>();

        while (sqlRowSet.next()) {
            Film film = Film.builder().
                    id(sqlRowSet.getInt("film_id")).
                    name(sqlRowSet.getString("film_name")).
                    description(sqlRowSet.getString("description")).
                    releaseDate(Objects.requireNonNull(sqlRowSet.getDate("release_date")).toLocalDate()).
                    duration(sqlRowSet.getInt("duration")).
                    mpa(Mpa.builder().
                            id(sqlRowSet.getInt("mpa_rating_id")).
                            name(sqlRowSet.getString("mpa_rating_name")).
                            build()).
                    build();
            films.add(film);
        }

        return films;
    }
}
