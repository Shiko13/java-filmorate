package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.CreationFailException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sqlQuery = "INSERT INTO reviews (film_id, user_id, is_positive, content) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setLong(1, review.getFilmID());
            stmt.setLong(2, review.getUserID());
            stmt.setBoolean(3, review.isPositive());
            stmt.setString(4, review.getContent());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() == null) throw new CreationFailException("Не удалось создать пользователя");

        return review.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE films SET film_id = ?, user_id = ?, is_positive = ?, content = ?, useful = ?, " +
                "WHERE review_id = ?";

        int updatedFilmId = jdbcTemplate.update(
                sqlQuery,
                review.getFilmID(),
                review.getUserID(),
                review.isPositive(),
                review.getContent(),
                review.getUseful(),
                review.getId()
        );

        if (updatedFilmId == 0) {
            throw new NotFoundException(String.format("Ревью c ID № %s не существует", review.getId()));
        }

        return review;
    }

    @Override
    public void remove(long reviewID) {

    }

    @Override
    public Review getByID(long reviewID) {
        return null;
    }

    @Override
    public List<Review> getAllByFilmID(long filmID, int count) {
        return null;
    }

    @Override
    public List<Review> getAll(int count) {
        return null;
    }

    @Override
    public void addLike(long reviewID, long userID) {

    }

    @Override
    public void addDislike(long reviewID, long userID) {

    }

    @Override
    public void removeLike(long reviewID, long userID) {

    }

    @Override
    public void removeDislike(long reviewID, long userID) {

    }
}
