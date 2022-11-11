package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> readAllMpaRatings() {
        String sqlQuery = "select * from mpa_ratings";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating);
    }

    @Override
    public Mpa readMpaRatingById(int mpaRatingId) {
        log.debug("Start storage with id = {}", mpaRatingId);
        String sqlQuery = "select * from mpa_ratings where mpa_rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpaRating, mpaRatingId);
    }

    private Mpa mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder().
                id(resultSet.getInt("mpa_rating_id")).
                name(resultSet.getString("mpa_rating_name")).
                build();
    }
}
