package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewsLikeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewsLikeService reviewsLikeService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(path = "/{id}")
    public void remove(@PathVariable ("id") long reviewId) {
        reviewService.remove(reviewId);
    }

    @GetMapping(path = "/{id}")
    public Review getByID(@PathVariable ("id") long reviewId) {
        return reviewService.getByID(reviewId);
    }

    @GetMapping
    public List<Review> getAllByFilmID(@RequestParam (required = false) @Positive Long filmId,
                                       @RequestParam (defaultValue = "10") @Positive int count) {
        return reviewService.getAllByFilmID(filmId, count);
    }

    @PutMapping(path = "/{id}/like/{userId}")
    public void addLike(@PathVariable ("id") long reviewId,
                        @PathVariable ("userId") long userId) {
        reviewsLikeService.add(reviewId, userId, true);
    }

    @PutMapping(path = "/{id}/dislike/{userId}")
    public void addDislike(@PathVariable ("id") long reviewId,
                           @PathVariable ("userId") long userId) {
        reviewsLikeService.add(reviewId, userId, false);
    }

    @DeleteMapping(path = "/{id}/like/{userId}")
    public void removeLike(@PathVariable ("id") long reviewId,
                           @PathVariable ("userId") long userId) {
        reviewsLikeService.remove(reviewId, userId, true);
    }

    @DeleteMapping(path = "/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable ("id") long reviewId,
                              @PathVariable ("userId") long userId) {
        reviewsLikeService.remove(reviewId, userId, false);
    }
}
