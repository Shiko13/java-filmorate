package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.CreationFailException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.LikeMapper;
import ru.yandex.practicum.filmorate.storage.dao.mappers.ReviewMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sqlQuery = "INSERT INTO reviews (film_id, user_id, is_positive, content) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
                stmt.setLong(1, review.getFilmId());
                stmt.setLong(2, review.getUserId());
                stmt.setBoolean(3, review.isPositive());
                stmt.setString(4, review.getContent());
                return stmt;
            }, keyHolder);
        } catch (Exception e) {
            throw new NotFoundException(String.format("Пользователь c ID № %s или фильм c ID № %s не существует", review.getUserId(), review.getFilmId()));
        }
        if (keyHolder.getKey() == null) throw new CreationFailException("Не удалось создать отзыв");

        return review.toBuilder().id(keyHolder.getKey().longValue()).build();
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE reviews SET film_id = ?, user_id = ?, is_positive = ?, content = ?, useful = ? " +
                "WHERE review_id = ?";

        int updatedReviewId = jdbcTemplate.update(
                sqlQuery,
                review.getFilmId(),
                review.getUserId(),
                review.isPositive(),
                review.getContent(),
                review.getUseful(),
                review.getId()
        );

        if (updatedReviewId == 0) {
            throw new NotFoundException(String.format("Ревью c ID № %s не существует", review.getId()));
        }

        return review;
    }

    @Override
    public void remove(long reviewID) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, reviewID);
    }

    @Override
    public Review getByID(long reviewID) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";

        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
                    stmt.setLong(1, reviewID);
                    return stmt;
                }, new ReviewMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Ревью c ID № %s не существует", reviewID))
                );
    }

    @Override
    public List<Review> getAllByFilmID(long filmID, int count) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";

        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setLong(1, filmID);
            stmt.setInt(2, count);
            return stmt;
        }, new ReviewMapper());
    }

    @Override
    public List<Review> getAll(int count) {
        String sqlQuery = "SELECT * FROM reviews LIMIT ?";

        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setInt(1, count);
            return stmt;
        }, new ReviewMapper());
    }
}
