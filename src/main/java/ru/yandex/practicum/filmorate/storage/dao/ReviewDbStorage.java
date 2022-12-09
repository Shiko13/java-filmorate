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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;
    private final String[] PRIMARY_KEY = new String[]{"review_id"};
    private final String NOT_EXIST = "Ревью c ID № %s не существует";

    @Override
    public Review create(Review review) {
        String sqlQuery = "INSERT INTO reviews (film_id, user_id, is_positive, content) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, PRIMARY_KEY);
            stmt.setLong(1, review.getFilmId());
            stmt.setLong(2, review.getUserId());
            stmt.setBoolean(3, review.isPositive());
            stmt.setString(4, review.getContent());
            return stmt;
        }, keyHolder);

        Optional<Long> optionalId = Optional.of(keyHolder.getKey().longValue());
        return review.toBuilder().id(optionalId.orElseThrow(() -> new CreationFailException("Не удалось создать отзыв"))).build();
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
            throw new NotFoundException(String.format(NOT_EXIST, review.getId()));
        }

        return review;
    }

    @Override
    public void remove(long reviewId) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public Review getByID(long reviewId) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";

        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, PRIMARY_KEY);
                    stmt.setLong(1, reviewId);
                    return stmt;
                }, reviewMapper)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format(NOT_EXIST, reviewId))
                );
    }

    @Override
    public List<Review> getAllByFilmID(long filmId, int count) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";

        List<Review> reviews = jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, PRIMARY_KEY);
            stmt.setLong(1, filmId);
            stmt.setInt(2, count);
            return stmt;
        }, reviewMapper);

        reviews = sortReviews(reviews);
        return reviews;
    }

    @Override
    public List<Review> getAll(int count) {
        String sqlQuery = "SELECT * FROM reviews LIMIT ?";

        List<Review> reviews = jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, PRIMARY_KEY);
            stmt.setInt(1, count);
            return stmt;
        }, reviewMapper);

        reviews = sortReviews(reviews);
        return reviews;
    }

    @Override
    public void updateRating(long reviewId) {
        String sqlQuery = "UPDATE reviews AS r SET useful = " +
                "(SELECT COUNT(l.user_id) from reviews_likes AS l  " +
                "WHERE l.review_id = r.review_id AND l.is_positive = true) - " +
                "(SELECT COUNT(d.user_id) from reviews_likes AS d " +
                "WHERE d.review_id = d.review_id AND d.is_positive = false) " +
                "WHERE r.review_id = ?";

        int updatedReviewId = jdbcTemplate.update(sqlQuery, reviewId);
        if (updatedReviewId == 0) {
            throw new NotFoundException(String.format(NOT_EXIST, reviewId));
        }

        log.debug("Рейтинг отзыва id = {} изменен", reviewId);
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
