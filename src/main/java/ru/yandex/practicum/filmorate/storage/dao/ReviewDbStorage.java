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
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.ReviewMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setLong(1, review.getFilmId());
            stmt.setLong(2, review.getUserId());
            stmt.setBoolean(3, review.isPositive());
            stmt.setString(4, review.getContent());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() == null) throw new CreationFailException("Не удалось создать отзыв");

        return review.toBuilder().id(keyHolder.getKey().longValue()).build();
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE reviews SET is_positive = ?, content = ?" +
                "WHERE review_id = ?";

        int updatedReviewId = jdbcTemplate.update(
                sqlQuery,
                review.isPositive(),
                review.getContent(),
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

        List<Review> reviews = jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setLong(1, filmID);
            stmt.setInt(2, count);
            return stmt;
        }, new ReviewMapper());

        reviews = sortReviews(reviews);
        return reviews;
    }

    @Override
    public List<Review> getAll(int count) {
        String sqlQuery = "SELECT * FROM reviews LIMIT ?";

        List<Review> reviews = jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setInt(1, count);
            return stmt;
        }, new ReviewMapper());

        reviews = sortReviews(reviews);
        return reviews;
    }

    @Override
    public void increaseRating(long reviewID) {
        String sqlQuery = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        realiseRatingQuery(sqlQuery, reviewID);
        log.debug("увеличен рейтинг отзывова id = {}", reviewID);
    }

    @Override
    public void reduceRating(long reviewID) {
        String sqlQuery = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        realiseRatingQuery(sqlQuery, reviewID);
        log.debug("уменьшин рейтинг отзывова id = {}", reviewID);
    }

    private void realiseRatingQuery(String sqlQuery, long reviewID) {
        int updatedReviewId = jdbcTemplate.update(sqlQuery, reviewID);
        if (updatedReviewId == 0) {
            throw new NotFoundException(String.format("Ревью c ID № %s не существует", reviewID));
        }
    }

    private List<Review> sortReviews(List<Review> reviews) {
        List<Review> positiveUseful = reviews.stream()
                .filter(o -> o.getUseful() >= 0)
                .sorted((o1, o2) -> (int) (o2.getUseful() - o1.getUseful()))
                .collect(Collectors.toList());

        List<Review> negativeUseful = reviews.stream()
                .filter(o -> o.getUseful() < 0)
                .sorted((o1, o2) -> (int) (o2.getUseful() - o1.getUseful()))
                .collect(Collectors.toList());

        return Stream.concat(positiveUseful.stream(), negativeUseful.stream()).collect(Collectors.toList());
    }
}
