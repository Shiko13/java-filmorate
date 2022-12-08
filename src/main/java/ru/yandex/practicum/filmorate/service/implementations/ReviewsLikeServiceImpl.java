package ru.yandex.practicum.filmorate.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CreationFailException;
import ru.yandex.practicum.filmorate.service.ReviewsLikeService;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeStorage;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewsLikeServiceImpl implements ReviewsLikeService {
    private final ReviewStorage reviewStorage;
    private final ReviewsLikeStorage reviewsLikeStorage;

    @Override
    public void add(long reviewId, long userId, boolean isPositive) {
        if (checkExistence(reviewId, userId, isPositive)) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно поставить оценку, т.к. пользователь ID №%s уже оценил отзыв ID №%s",
                            userId, reviewId
                    )
            );
        }

        boolean isSuccess = reviewsLikeStorage.add(reviewId, userId, isPositive);
        if (isSuccess) {
            log.debug("Добавлена оценка пользователя с ID №{} отзыву №{}", userId, reviewId);
            reviewStorage.updateRating(reviewId);
        }
    }

    @Override
    public void remove(long reviewId, long userId, boolean isPositive) {
        if (!checkExistence(reviewId, userId, isPositive)) {
            throw new CreationFailException(
                    String.format(
                            "Невозможно удалить оценку, т.к. пользователь ID №%s не оценивал отзыв ID №%s",
                            userId, reviewId
                    )
            );
        }

        boolean isSuccess = reviewsLikeStorage.remove(reviewId, userId, isPositive);
        if (isSuccess) {
            log.debug("Удалена оценка пользователя с ID №{} отзыву №{}", userId, reviewId);
            reviewStorage.updateRating(reviewId);
        }
    }

    private boolean checkExistence(long reviewId, long userId, boolean isPositive) {
        return reviewsLikeStorage.get(reviewId, userId, isPositive) != null;
    }
}
