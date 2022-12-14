package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.TypeOfEvent;
import ru.yandex.practicum.filmorate.model.TypeOfOperation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserEventListStorage userEventListStorage;

    @Override
    public Review create(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());

        review = reviewStorage.create(review);
        log.debug("Добавлен отзыв к фильму id = {}", review.getFilmId());

        userEventListStorage.addEvent(review.getUserId(), String.valueOf(TypeOfEvent.REVIEW),
                String.valueOf(TypeOfOperation.ADD), review.getId());

        return review;
    }

    @Override
    public Review update(Review review) {
        review = reviewStorage.update(review);
        log.debug("Отредактирован отзыв к фильму id = {}", review.getFilmId());

        userEventListStorage.addEvent(getByID(review.getId()).getUserId(), String.valueOf(TypeOfEvent.REVIEW),
                String.valueOf(TypeOfOperation.UPDATE), review.getId());

        return review;
    }

    @Override
    public void remove(long reviewId) {
        userEventListStorage.addEvent(getByID(reviewId).getUserId(), String.valueOf(TypeOfEvent.REVIEW),
                String.valueOf(TypeOfOperation.REMOVE), reviewId);

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
        if (filmId == null) {
            reviews = reviewStorage.getAll(count);
            log.debug("Возвращен список всех отзывов");
        } else {
            reviews = reviewStorage.getAllByFilmID(filmId, count);
            log.debug("Возвращен список отзывов к фильму id = {}", filmId);
        }
        return reviews;
    }
}
