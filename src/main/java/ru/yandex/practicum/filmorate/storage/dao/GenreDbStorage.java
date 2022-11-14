package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> readAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRow);
    }

    @Override
    public Genre readById(long genreId) {
        String sqlQuery = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, GenreDbStorage::mapRow, genreId);
    }

    @Override
    public List<Film> set(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from GENRES g, " +
                "FILM_GENRES fg where fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID in (" + inSql + ")";

        List<Long> ids = new ArrayList<>(filmById.keySet());

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, ids.toArray());

        while (sqlRowSet.next()) {
            System.out.println(sqlRowSet.getLong("film_id") + " " + sqlRowSet.getInt("genre_id") +  " " +
                    sqlRowSet.getString("genre_name"));
            final Film film = filmById.get(sqlRowSet.getLong("film_id"));
            Genre genre = Genre.builder().
                        id(sqlRowSet.getInt("genre_id")).
                        name(sqlRowSet.getString("genre_name")).
                        build();
            film.getGenres().add(genre);
        }
        return films;
    }

    private static Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder().
                id(resultSet.getInt("genre_id")).
                name(resultSet.getString("genre_name")).
                build();
    }
}