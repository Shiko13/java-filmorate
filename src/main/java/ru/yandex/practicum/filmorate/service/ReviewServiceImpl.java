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
    public void remove(long reviewID) {
        reviewStorage.remove(reviewID);
        log.debug("Удален отзыв id = {}", reviewID);
    }

    @Override
    public Review getByID(long reviewID) {
        Review review = reviewStorage.getByID(reviewID);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmId());
        return review;
    }

    @Override
    public List<Review> getAllByFilmID(Long filmID, int count) {
        List<Review> reviews;
        if (filmID < 0) {
            reviews = reviewStorage.getAll(count);
            log.debug("Возвращен список всех отзывов");
        } else {
            reviews = reviewStorage.getAllByFilmID(filmID, count);
            log.debug("Возвращен список отзывов к фильму id = {}", filmID);
        }
        return reviews;
    }

    @Override
    public void addLike(long reviewID, long userID) {
        Like like = reviewsLikeStorage.getLikeById(reviewID, userID);
        if (like != null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк отзыву ID №%s",
                            userID, reviewID)
            );
        }

        boolean isSuccess = reviewsLikeStorage.addLike(reviewID, userID);
        if (isSuccess) {
            reviewStorage.increaseRating(reviewID);
            log.debug("Добавлен лайк отзывову id = {}", reviewID);
        }
    }

    @Override
    public void addDislike(long reviewID, long userID) {
        Like like = reviewsLikeStorage.getDislikeById(reviewID, userID);
        if (like != null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно поставить дизлайк, т.к. пользователь ID №%s уже поставил лайк отзыву ID №%s",
                            userID, reviewID)
            );
        }

        boolean isSuccess = reviewsLikeStorage.addDislike(reviewID, userID);
        if (isSuccess) {
            log.debug("Добавлен дизлайк отзывову id = {}", reviewID);
            reviewStorage.reduceRating(reviewID);
        }
    }

    @Override
    public void removeLike(long reviewID, long userID) {
        Like like = reviewsLikeStorage.getLikeById(reviewID, userID);
        if (like == null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк отзыву ID №%s",
                            userID, reviewID)
            );
        }

        boolean isSuccess = reviewsLikeStorage.removeLike(reviewID, userID);
        if (isSuccess) {
            log.debug("Удален лайк отзывову id = {}", reviewID);
            reviewStorage.reduceRating(reviewID);
        }
    }

    @Override
    public void removeDislike(long reviewID, long userID) {
        Like like = reviewsLikeStorage.getDislikeById(reviewID, userID);
        if (like == null) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно удалить дизлайк, т.к. пользователь ID №%s не ставил дизлайк отзыву ID №%s",
                            userID, reviewID)
            );
        }

        boolean isSuccess = reviewsLikeStorage.removeDislike(reviewID, userID);
        if (isSuccess) {
            reviewStorage.increaseRating(reviewID);
            log.debug("Удален дизлайк отзывову id = {}", reviewID);
        }
    }
}
