package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Like create(long filmId, long userId) {
        String sqlQuery = "insert into likes values (?, ?)";

        int numRow = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("Film with id = %d or user with id = %d not found", filmId, userId));
        }

        return Like.builder().
                entityId(filmId).
                userId(userId).
                build();

    }

    @Override
    public void delete(long filmId, long userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";

        int numRow = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("Film with id = %d or user with id = %d not found", filmId, userId));
        }
    }

    @Override
    public List<Long> getRecommendations(long userId) {
        String sqlQueryUser = "select l3.film_id from\n" +
                "(select l1.user_id, count(l1.film_id) as count from likes as l\n" +
                "left join likes as l1 on l.film_id = l1.film_id\n" +
                "where l.user_id = ? and l.user_id <> l1.user_id\n" +
                "group by l1.user_id order by count desc limit 1) as sub\n" +
                "left join likes as l3 on sub.user_id = l3.user_id\n" +
                "where l3.film_id not in (select film_id from likes where user_id = ?)";

        return jdbcTemplate.query(sqlQueryUser,
                (rs, rowNum) -> rs.getLong("FILM_ID"), userId, userId);
    }
}