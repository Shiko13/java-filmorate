package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film findById(long filmId) {
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
    public List<Film> findAll() {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "mr.MPA_RATING_ID, mr.MPA_RATING_NAME\n" +
                "from films as f\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> films = mapRowToFilmSet(sqlRowSet);

        return new ArrayList<>(films);
    }

    @Override
    public Film create(Film film) {
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
        createDirectors(film);

        return film;
    }

    private void createGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }

        List<Genre> genres = film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "insert into film_genres values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });

        film.setGenres(genres);
    }

    private void updateGenres(Film film) {
        String sqlQuery = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        createGenres(film);
    }

    private void createDirectors(Film film) {
        if (film.getDirectors() == null) {
            return;
        }

        List<Director> directors = film.getDirectors().stream()
                .distinct()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "insert into film_directors values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, directors.get(i).getId());
                    }

                    public int getBatchSize() {
                        return directors.size();
                    }
                });

        film.setDirectors(directors);
    }

    private void updateDirectors(Film film) {
        String sqlQuery = "delete from film_directors where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        createDirectors(film);
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "update films set film_name = ?, description = ?, release_date = ?, duration = ?, " +
                "MPA_RATING = ? where film_id = ?";

        int numRow = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (numRow == 0) {
            throw new NotFoundException("Film with those parameters not allow updated");
        }

        updateGenres(film);
        updateDirectors(film);
    }

    @Override
    public void deleteById(long filmId) {
        String sqlQuery = "delete from films where film_id = ?";

        int numRow = jdbcTemplate.update(sqlQuery, filmId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "delete from films";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<Film> getSortByYearFromDirector(long directorId) {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "mr.MPA_RATING_ID, mr.MPA_RATING_NAME, fd.DIRECTOR_ID\n" +
                "from films as f\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n" +
                "join FILM_DIRECTORS as fd on f.FILM_ID = fd.FILM_ID\n" +
                "where fd.director_id = ?" +
                "order by f.RELEASE_DATE";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, directorId);
        validateDirectorId(directorId, sqlRowSet);

        Set<Film> films = mapRowToFilmSet(sqlRowSet);
        return new ArrayList<>(films);
    }

    @Override
    public List<Film> getSortByLikesFromDirector(long directorId) {
        String sqlQuery = "select f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "mr.MPA_RATING_ID, mr.MPA_RATING_NAME, fd.DIRECTOR_ID, " +
                "count(l.USER_ID)\n" +
                "from films as f\n" +
                "left join LIKES as l on f.FILM_ID = l.FILM_ID\n" +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID\n" +
                "join FILM_DIRECTORS as fd on f.FILM_ID = fd.FILM_ID\n" +
                "where fd.director_id = ?" +
                "group by f.FILM_ID " +
                "order by count(l.USER_ID)";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, directorId);
        validateDirectorId(directorId, sqlRowSet);

        Set<Film> films = mapRowToFilmSet(sqlRowSet);
        return new ArrayList<>(films);
    }

    @Override
    public Set<Film> getCommon(long userId, long friendId) {
        String sqlQuery = "SELECT * FROM films AS f " +
                "INNER JOIN likes AS a ON a.film_id = f.film_id " +
                "INNER JOIN likes AS b ON b.film_id = f.film_id " +
                "INNER JOIN mpa_ratings AS с ON с.mpa_rating_id = f.mpa_rating " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS rate FROM likes GROUP BY film_id) AS fl " +
                "ON (fl.film_id = f.film_id) " +
                "WHERE a.user_id = ? AND b.user_id = ?" +
                "ORDER BY fl.rate DESC";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return mapRowToFilmSet(sqlRowSet);
    }

    @Override
    public Set<Film> getTopPopular(Long genreId, Integer releaseYear, int count) {
        String sqlQuery;
        if (genreId == null && releaseYear == null) {
            sqlQuery = getTopPopularQuery(count);
        } else if (releaseYear == null) {
            sqlQuery = getTopPopularQuery(genreId, count);
        } else if (genreId == null) {
            sqlQuery = getTopPopularQuery(releaseYear, count);
        } else {
            sqlQuery = getTopPopularQuery(genreId, releaseYear, count);
        }

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        return mapRowToFilmSet(sqlRowSet);
    }

    @Override
    public List<Film> searchFilmsByTitleAndDirector(String query) {
        String sqlQuery = "SELECT DISTINCT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                        "mr.mpa_rating_id, mr.mpa_rating_name, fl.rate "+
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS mr ON (mr.mpa_rating_id = f.mpa_rating) " +
                        "LEFT JOIN " +
                        "(SELECT film_id, COUNT(user_id) AS rate " +
                        "FROM likes " +
                        "GROUP BY film_id) AS fl ON (fl.film_id = f.film_id) " +
                        "LEFT JOIN film_directors AS fd ON (fd.film_id = f.film_id)" +
                        "LEFT JOIN directors AS d ON (fd.director_id = d.director_id) " +
                        "WHERE " +
                        "LOWER(f.film_name) like '%" + query.toLowerCase() + "%' " +
                        " OR " +
                        "LOWER(d.director_name) like '%" + query.toLowerCase() + "%' " +
                        "ORDER BY fl.rate DESC ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> films = mapRowToFilmSet(sqlRowSet);
        return new ArrayList<>(films);
    }
    
    @Override
    public List<Film> searchFilmsByTitle(String query) {
        String sqlQuery = "SELECT DISTINCT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "mr.mpa_rating_id, mr.mpa_rating_name, fl.rate "+
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON (mr.mpa_rating_id = f.mpa_rating) " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS rate " +
                "FROM likes " +
                "GROUP BY film_id) AS fl ON (fl.film_id = f.film_id) " +
                "LEFT JOIN film_directors AS fd ON (fd.film_id = f.film_id)" +
                "LEFT JOIN directors AS d ON (fd.director_id = d.director_id) " +
                "WHERE " +
                "LOWER(f.film_name) like '%" + query.toLowerCase() + "%' " +
                "ORDER BY fl.rate DESC ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> films = mapRowToFilmSet(sqlRowSet);
        return new ArrayList<>(films);
    }
    
    @Override
    public List<Film> searchFilmsByDirector(String query) {
        String sqlQuery = "SELECT DISTINCT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "mr.mpa_rating_id, mr.mpa_rating_name, fl.rate "+
                "FROM films AS f " +
                "JOIN mpa_ratings AS mr ON (mr.mpa_rating_id = f.mpa_rating) " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS rate " +
                "FROM likes " +
                "GROUP BY film_id) AS fl ON (fl.film_id = f.film_id) " +
                "LEFT JOIN film_directors AS fd ON (fd.film_id = f.film_id)" +
                "LEFT JOIN directors AS d ON (fd.director_id = d.director_id) " +
                "WHERE " +
                "LOWER(d.director_name) like '%" + query.toLowerCase() + "%' " +
                "ORDER BY fl.rate DESC ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);
        Set<Film> films = mapRowToFilmSet(sqlRowSet);
        return new ArrayList<>(films);
    }

    private static void validateDirectorId(long directorId, SqlRowSet sqlRowSet) {
        sqlRowSet.last();
        int resultCount = sqlRowSet.getRow();
        if (resultCount == 0) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }
        sqlRowSet.beforeFirst();
    }

    private static Film mapRowToFilm(SqlRowSet sqlRowSet) {
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

        return film;
    }

    private static Set<Film> mapRowToFilmSet(SqlRowSet sqlRowSet) {
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

    private String getTopPopularQuery() {
        return "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, mr.MPA_RATING_ID, " +
                "mr.MPA_RATING_NAME from FILMS as f left join LIKES as l on f.FILM_ID = l.FILM_ID " +
                "join mpa_ratings as mr on f.MPA_RATING = mr.MPA_RATING_ID ";
    }

    private String getTopPopularQuery(int count) {
        return getTopPopularQuery() + "group by f.film_id order by count(l.FILM_ID) desc limit " + count;
    }

    private String getTopPopularQuery(Long genreId, int count) {
        return getTopPopularQuery() +
                "join film_genres as fg on f.film_id = fg.film_id " +
                "join genres as g on g.genre_id = fg.genre_id where g.genre_id = " + genreId + " " +
                "group by f.film_id order by count(l.FILM_ID) desc limit " + count;
    }

    private String getTopPopularQuery(Integer releaseYear, int count) {
        return getTopPopularQuery() +
                "where extract (year from f.RELEASE_DATE) = " + releaseYear + " " +
                "group by f.film_id order by count(l.FILM_ID) desc limit " + count;
    }

    private String getTopPopularQuery(Long genreId, Integer releaseYear, int count) {
        return getTopPopularQuery() +
                "join film_genres as fg on f.film_id = fg.film_id " +
                "join genres as g on g.genre_id = fg.genre_id " +
                "where g.genre_id = " + genreId + " AND " +
                "extract (year from f.RELEASE_DATE) = " + releaseYear + " " +
                "group by f.film_id order by count(l.FILM_ID) desc limit " + count;
    }
}
