package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
@AllArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Like createLike(long filmId, long userId) {
        String sqlQuery = "insert into likes values (?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, userId);
        return Like.builder().
                filmId(filmId).
                userId(userId).
                build();
    }


    @Override
    public void deleteLike(long filmId, long userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }
}
