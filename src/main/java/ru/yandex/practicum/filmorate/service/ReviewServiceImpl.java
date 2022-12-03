package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CreationFailException;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewServiceImpl implements ReviewService{
    private final ReviewStorage reviewStorage;
    private final ReviewsLikeStorage reviewsLikeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review create(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());

        review = reviewStorage.create(review);
        log.debug("Добавлен отзыв к фильму id = {}", review.getFilmId());
        return review;
    }

    @Override
    public Review update(Review review) {
        review = reviewStorage.update(review);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmId());
        return review;
    }

    @Override
    public void remove(long reviewId) {
        reviewStorage.remove(reviewId);
        log.debug("Удален отзыв id = {}", reviewId);
    }

    @Override
    public Review getByID(long reviewId) {
        Review review = reviewStorage.getByID(reviewId);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmId());
        return review;
    }

    @Override
    public List<Review> getAllByFilmID(Long filmId, int count) {
        List<Review> reviews;
        if (filmId < 0) {
            reviews = reviewStorage.getAll(count);
            log.debug("Возвращен список всех отзывов");
        } else {
            reviews = reviewStorage.getAllByFilmID(filmId, count);
            log.debug("Возвращен список отзывов к фильму id = {}", filmId);
        }
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        Like like = reviewsLikeStorage.getLikeById(reviewId, userId);
        if (like != null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк отзыву ID №%s",
                            userId, reviewId)
            );
        }

        boolean isSuccess = reviewsLikeStorage.addLike(reviewId, userId);
        if (isSuccess) {
            reviewStorage.increaseRating(reviewId);
            log.debug("Добавлен лайк отзывову id = {}", reviewId);
        }
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        Like like = reviewsLikeStorage.getDislikeById(reviewId, userId);
        if (like != null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно поставить дизлайк, т.к. пользователь ID №%s уже поставил лайк отзыву ID №%s",
                            userId, reviewId)
            );
        }

        boolean isSuccess = reviewsLikeStorage.addDislike(reviewId, userId);
        if (isSuccess) {
            log.debug("Добавлен дизлайк отзывову id = {}", reviewId);
            reviewStorage.reduceRating(reviewId);
        }
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        Like like = reviewsLikeStorage.getLikeById(reviewId, userId);
        if (like == null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк отзыву ID №%s",
                            userId, reviewId)
            );
        }

        boolean isSuccess = reviewsLikeStorage.removeLike(reviewId, userId);
        if (isSuccess) {
            log.debug("Удален лайк отзывову id = {}", reviewId);
            reviewStorage.reduceRating(reviewId);
        }
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        Like like = reviewsLikeStorage.getDislikeById(reviewId, userId);
        if (like == null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно удалить дизлайк, т.к. пользователь ID №%s не ставил дизлайк отзыву ID №%s",
                            userId, reviewId)
            );
        }

        boolean isSuccess = reviewsLikeStorage.removeDislike(reviewId, userId);
        if (isSuccess) {
            reviewStorage.increaseRating(reviewId);
            log.debug("Удален дизлайк отзывову id = {}", reviewId);
        }
    }
}
