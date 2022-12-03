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

    @Override
    public boolean addLike(long reviewID, long userID) {
        String sqlQuery = "INSERT INTO reviews_likes VALUES (?, ?)";
        realiseUpdateQuery(sqlQuery, reviewID, userID);
        log.debug("добавлен лайк пользователя с ID №{} отзыву №{}", userID, reviewID);
        return true;
    }

    @Override
    public boolean addDislike(long reviewID, long userID) {
        String sqlQuery = "INSERT INTO reviews_dislikes VALUES (?, ?)";
        realiseUpdateQuery(sqlQuery, reviewID, userID);
        log.debug("добавлен дизлайк пользователя с ID №{} отзыву №{}", userID, reviewID);
        return true;
    }

    @Override
    public boolean removeLike(long reviewID, long userID) {
        String sqlQuery = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        realiseUpdateQuery(sqlQuery, reviewID, userID);
        log.debug("удален лайк пользователя с ID №{} отзыву №{}", userID, reviewID);
        return true;
    }

    @Override
    public boolean removeDislike(long reviewID, long userID) {
        String sqlQuery = "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ?";
        realiseUpdateQuery(sqlQuery, reviewID, userID);
        log.debug("удален дизлайк пользователя с ID №{} отзыву №{}", userID, reviewID);
        return true;
    }

    @Override
    public Like getLikeById(long reviewID, long userID) {
        String sqlQuery = "SELECT * FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        return realiseGetQuery(sqlQuery, reviewID, userID);
    }

    @Override
    public Like getDislikeById(long reviewID, long userID) {
        String sqlQuery = "SELECT * FROM reviews_dislikes WHERE review_id = ? AND user_id = ?";
        return realiseGetQuery(sqlQuery, reviewID, userID);
    }

    private Like realiseGetQuery(String sqlQuery, long reviewID, long userID) {
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id", "user_id"});
                    stmt.setLong(1, reviewID);
                    stmt.setLong(2, userID);
                    return stmt;
                }, new LikeMapper())
                .stream()
                .findFirst()
                .orElse(null);
    }

    private void realiseUpdateQuery(String sqlQuery, long reviewID, long userID) {
        jdbcTemplate.update(sqlQuery, reviewID, userID);
    }
}
