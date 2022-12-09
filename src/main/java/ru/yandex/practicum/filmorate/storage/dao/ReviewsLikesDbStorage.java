package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.LikeMapper;

import java.sql.PreparedStatement;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewsLikesDbStorage implements ReviewsLikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikeMapper likeMapper;

    @Override
    public boolean add(long reviewId, long userId, boolean isPositive) {
        String sqlQuery = "INSERT INTO reviews_likes VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isPositive);
        return true;
    }

    @Override
    public boolean remove(long reviewId, long userId, boolean isPositive) {
        String sqlQuery = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_positive = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isPositive);
        return true;
    }

    @Override
    public Like get(long reviewId, long userId, boolean isPositive) {
        String sqlQuery = "SELECT * FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_positive = ?";

        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id", "user_id"});
                    stmt.setLong(1, reviewId);
                    stmt.setLong(2, userId);
                    stmt.setBoolean(3, isPositive);
                    return stmt;
                }, likeMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
