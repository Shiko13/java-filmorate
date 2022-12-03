package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewService reviewService;
    private final ReviewsLikeStorage reviewsLikeStorage;

    @Test
    public void fieldsReviewCorrect() {
        User user = User.builder()
                .id(1L)
                .email("lisaann@ya.ru")
                .login("gape")
                .name("Lisa")
                .birthday(LocalDate.of(1972, 5, 9))
                .build();
        userStorage.create(user);

        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Film film = Film.builder()
                .id(1L)
                .name("Hot in Las Vegas")
                .description("Casino and some problems")
                .releaseDate(LocalDate.of(1994, 10, 10))
                .duration(69)
                .mpa(mpa)
                .build();
        filmStorage.create(film);

        Review review = Review.builder()
                .id(1L)
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .content("AAA")
                .build();

        Optional<Review> optionalReview = Optional.of(reviewStorage.create(review));
        assertThat(optionalReview)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("userId", 1L)
                        .hasFieldOrPropertyWithValue("filmId", 1L)
                        .hasFieldOrPropertyWithValue("isPositive", true)
                        .hasFieldOrPropertyWithValue("useful", 0L)
                        .hasFieldOrPropertyWithValue("content", "AAA"));

        Optional<Review> optionalReview1 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview1)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("userId", 1L)
                        .hasFieldOrPropertyWithValue("filmId", 1L)
                        .hasFieldOrPropertyWithValue("isPositive", true)
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("useful", 0L)
                        .hasFieldOrPropertyWithValue("content", "AAA"));

        Review review1 = Review.builder()
                .id(2L)
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .content("CCC")
                .build();
        reviewStorage.create(review1);

        List<Review> reviews = reviewStorage.getAll(10);
        assertThat(reviews)
                .hasSize(2)
                .hasSameElementsAs(List.of(review, review1));

        List<Review> reviews1 = reviewStorage.getAllByFilmID(1, 10);
        assertThat(reviews1)
                .hasSize(2)
                .hasSameElementsAs(List.of(review, review1));

        reviewStorage.remove(2);
        List<Review> reviews2 = reviewStorage.getAll(10);
        assertThat(reviews2)
                .hasSize(1);

        review = review.toBuilder().content("BBB").build();
        Optional<Review> optionalReview2 = Optional.of(reviewStorage.update(review));
        assertThat(optionalReview2)
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("content", "BBB"));

        reviewService.addLike(1, 1);

        Optional<Review> optionalReview3 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview3)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", 1L));

        Optional<Like> optionalLike = Optional.of(reviewsLikeStorage.getLikeById(1, 1));
        assertThat(optionalLike)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("entityId", 1L)
                        .hasFieldOrPropertyWithValue("userId", 1L));

        reviewService.removeLike(1,1);
        Optional<Review> optionalReview4 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview4)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", 0L));

        reviewService.addDislike(1, 1);
        Optional<Review> optionalReview5 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview5)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", -1L));

        reviewService.removeDislike(1,1);
        Optional<Review> optionalReview6 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview6)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", 0L));
    }
}
