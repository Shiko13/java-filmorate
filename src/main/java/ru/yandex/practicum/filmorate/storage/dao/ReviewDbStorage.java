package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public Review update(Review review) {
        return null;
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
