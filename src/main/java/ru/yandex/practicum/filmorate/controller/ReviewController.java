package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        if (review.getFilmId() == 0 || review.getUserId() == 0) {
            throw new ValidationException("Поля filmId и userId должны быть заполнены");
        }
        if (review.getIsPositive() == null) throw new ValidationException("Поле isPositive должно быть заполнено");
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(path = "/{id}")
    public void remove(@PathVariable ("id") long reviewID) {
        reviewService.remove(reviewID);
    }

    @GetMapping(path = "/{id}")
    public Review getByID(@PathVariable ("id") long reviewID) {
        return reviewService.getByID(reviewID);
    }

    @GetMapping
    public List<Review> getAllByFilmID(@RequestParam (defaultValue = "-1") Long filmId,
                                       @RequestParam (defaultValue = "10") @Positive int count) {
        return reviewService.getAllByFilmID(filmId, count);
    }

    @PutMapping(path = "/{id}/like/{userId}")
    public void addLike(@PathVariable ("id") long reviewID,
                        @PathVariable ("userId") long userID) {
        reviewService.addLike(reviewID, userID);
    }

    @PutMapping(path = "/{id}/dislike/{userId}")
    public void addDislike(@PathVariable ("id") long reviewID,
                           @PathVariable ("userId") long userID) {
        reviewService.addDislike(reviewID, userID);
    }

    @DeleteMapping(path = "/{id}/like/{userId}")
    public void removeLike(@PathVariable ("id") long reviewID,
                           @PathVariable ("userId") long userID) {
        reviewService.removeLike(reviewID, userID);
    }

    @DeleteMapping(path = "/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable ("id") long reviewID,
                              @PathVariable ("userId") long userID) {
        reviewService.removeDislike(reviewID, userID);
    }

/*API:
1) POST /reviews Добавление нового отзыва. +
2) PUT /reviews Редактирование уже имеющегося отзыва. +
3) DELETE /reviews/{id} Удаление уже имеющегося отзыва. +
4) GET /reviews/{id} Получение отзыва по идентификатору. +
5) GET /reviews?filmId={filmId}&count={count} Получение всех отзывов по идентификатору
фильма, если фильм не указан то все. Если кол-во не указано то 10.+
6) PUT /reviews/{id}/like/{userId}  — пользователь ставит лайк отзыву.+
7) PUT /reviews/{id}/dislike/{userId}  — пользователь ставит дизлайк отзыву.
8) DELETE /reviews/{id}/like/{userId}  — пользователь удаляет лайк/дизлайк отзыву.
9) DELETE /reviews/{id}/dislike/{userId}  — пользователь удаляет дизлайк отзыву.*/
}
