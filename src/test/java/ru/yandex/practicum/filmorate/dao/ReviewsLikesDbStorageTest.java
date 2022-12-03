package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewsLikesDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewService reviewService;

    private final ReviewsLikeStorage reviewsLikeStorage;

    @BeforeEach
    public void fillTables() {
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
        reviewStorage.create(review);
    }

    @Test
    public void ReviewsLikesCorrect() {
        reviewService.addLike(1, 1);

        Optional<Review> optionalReview = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview)
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
        Optional<Review> optionalReview1 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview1)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", 0L));

        reviewService.addDislike(1, 1);
        Optional<Review> optionalReview2 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview2)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", -1L));

        reviewService.removeDislike(1,1);
        Optional<Review> optionalReview3 = Optional.of(reviewStorage.getByID(1));
        assertThat(optionalReview3)
                .isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("useful", 0L));
    }
}
