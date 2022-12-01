package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewServiceImpl implements ReviewService{
    private final ReviewDbStorage reviewDbStorage;

    @Override
    public Review create(Review review) {
        review = reviewDbStorage.create(review);
        log.debug("Добавлен отзыв к фильму id = {}", review.getFilmID());
        return review;
    }

    @Override
    public Review update(Review review) {
        review = reviewDbStorage.update(review);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmID());
        return review;
    }

    @Override
    public void remove(long reviewID) {
        reviewDbStorage.remove(reviewID);
        log.debug("Удален отзыв id = {}", reviewID);
    }

    @Override
    public Review getByID(long reviewID) {
        Review review = reviewDbStorage.getByID(reviewID);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmID());
        return review;
    }

    @Override
    public List<Review> getAllByFilmID(Integer filmID, int count) {
        List<Review> reviews;
        if (filmID == null) {
            reviews = reviewDbStorage.getAll(count);
            log.debug("Возвращен список всех отзывов");
        } else {
            reviews = reviewDbStorage.getAllByFilmID(filmID, count);
            log.debug("Возвращен список отзывов к фильму id = {}", filmID);
        }
        return reviews;
    }

    @Override
    public void addLike(long reviewID, long userID) {
        reviewDbStorage.addLike(reviewID, userID);
        log.debug("Добавлен лайк отзывову id = {}", reviewID);
    }

    @Override
    public void addDislike(long reviewID, long userID) {
        reviewDbStorage.addDislike(reviewID, userID);
        log.debug("Добавлен дизлайк отзывову id = {}", reviewID);
    }

    @Override
    public void removeLike(long reviewID, long userID) {
        reviewDbStorage.removeLike(reviewID, userID);
        log.debug("Удален лайк отзывову id = {}", reviewID);
    }

    @Override
    public void removeDislike(long reviewID, long userID) {
        reviewDbStorage.removeDislike(reviewID, userID);
        log.debug("Удален дизлайк отзывову id = {}", reviewID);
    }
}
