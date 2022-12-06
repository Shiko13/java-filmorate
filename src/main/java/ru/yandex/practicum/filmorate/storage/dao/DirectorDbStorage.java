package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Repository("directorDb")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private static void addDirectorToFilm(Map<Long, Film> filmById, SqlRowSet sqlRowSet) {
        while (sqlRowSet.next()) {
            final Film film = filmById.get(sqlRowSet.getLong("film_id"));
            Director director = Director.builder().
                    id(sqlRowSet.getLong("director_id")).
                    name(sqlRowSet.getString("director_name")).
                    build();
            film.getDirectors().add(director);
        }
    }

    private static Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder().
                id(resultSet.getLong("director_id")).
                name(resultSet.getString("director_name")).
                build();
    }

    @Override
    public List<Director> readAll() {
        String sqlQuery = "select * from DIRECTORS";
        return jdbcTemplate.query(sqlQuery, DirectorDbStorage::mapRow);
    }

    @Override
    public Director readById(long directorId) {
        String sqlQuery = "select * from DIRECTORS where DIRECTOR_ID = ?";

        Director director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, DirectorDbStorage::mapRow, directorId);
        } catch (Exception e) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }

        return director;
    }

    @Override
    public List<Film> set(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from DIRECTORS d, " +
                "FILM_DIRECTORS fd where fd.DIRECTOR_ID = d.DIRECTOR_ID AND fd.FILM_ID in (" + inSql + ")";

        List<Long> ids = new ArrayList<>(filmById.keySet());

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, ids.toArray());
        addDirectorToFilm(filmById, sqlRowSet);

        return films;

    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "insert into directors (director_name) values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"director_id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "update directors set director_name = ? where director_id = ?";

        int numRow = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        if (numRow == 0) {
            throw new NotFoundException("Director with those parameters not allow updated");
        }

        return director;
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "delete from directors";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void deleteById(long directorId) {
        String sqlQuery = "delete from directors where director_id = ?";
        int numRow = jdbcTemplate.update(sqlQuery, directorId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("Director with id = %d not found", directorId));
        }
    }
}
