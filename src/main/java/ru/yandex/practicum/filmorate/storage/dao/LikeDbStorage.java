package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

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
}