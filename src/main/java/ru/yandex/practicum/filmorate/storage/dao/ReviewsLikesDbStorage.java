package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.LikeMapper;

import java.sql.PreparedStatement;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ReviewsLikesDbStorage implements ReviewsLikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikeMapper likeMapper;

    @Override
    public boolean addLike(long reviewId, long userId) {
        String sqlQuery = "INSERT INTO reviews_likes VALUES (?, ?)";
        realiseUpdateQuery(sqlQuery, reviewId, userId);
        log.debug("добавлен лайк пользователя с ID №{} отзыву №{}", userId, reviewId);
        return true;
    }

    @Override
    public boolean addDislike(long reviewId, long userId) {
        String sqlQuery = "INSERT INTO reviews_dislikes VALUES (?, ?)";
        realiseUpdateQuery(sqlQuery, reviewId, userId);
        log.debug("добавлен дизлайк пользователя с ID №{} отзыву №{}", userId, reviewId);
        return true;
    }

    @Override
    public boolean removeLike(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        realiseUpdateQuery(sqlQuery, reviewId, userId);
        log.debug("удален лайк пользователя с ID №{} отзыву №{}", userId, reviewId);
        return true;
    }

    @Override
    public boolean removeDislike(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ?";
        realiseUpdateQuery(sqlQuery, reviewId, userId);
        log.debug("удален дизлайк пользователя с ID №{} отзыву №{}", userId, reviewId);
        return true;
    }

    @Override
    public Like getLikeById(long reviewId, long userId) {
        String sqlQuery = "SELECT * FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        return realiseGetQuery(sqlQuery, reviewId, userId);
    }

    @Override
    public Like getDislikeById(long reviewId, long userId) {
        String sqlQuery = "SELECT * FROM reviews_dislikes WHERE review_id = ? AND user_id = ?";
        return realiseGetQuery(sqlQuery, reviewId, userId);
    }

    private Like realiseGetQuery(String sqlQuery, long reviewId, long userId) {
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id", "user_id"});
                    stmt.setLong(1, reviewId);
                    stmt.setLong(2, userId);
                    return stmt;
                }, likeMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private void realiseUpdateQuery(String sqlQuery, long reviewId, long userId) {
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }
}
