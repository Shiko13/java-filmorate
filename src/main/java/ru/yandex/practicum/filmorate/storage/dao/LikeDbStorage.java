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
                filmId(filmId).
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
        String sqlQueryUser = "SELECT L.FILM_ID FROM\n" +
                "(SELECT L2.USER_ID, COUNT(L2.FILM_ID) CNT FROM LIKES L1\n" +
                "LEFT JOIN LIKES L2 ON L1.FILM_ID = L2.FILM_ID\n" +
                "WHERE L1.USER_ID = ? AND L1.USER_ID <> L2.USER_ID\n" +
                "GROUP BY L2.USER_ID ORDER BY CNT DESC LIMIT 1) U\n" +
                "LEFT JOIN LIKES L ON U.USER_ID = L.USER_ID\n" +
                "WHERE L.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES WHERE USER_ID = ?)";
        return jdbcTemplate.query(sqlQueryUser,
                (rs, rowNum) -> rs.getLong("FILM_ID"), userId, userId);
    }
}