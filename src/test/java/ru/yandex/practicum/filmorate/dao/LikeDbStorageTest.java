package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbStorageTest {
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void addLikeTest() {
        User user1 = User.builder()
                .id(1L)
                .email("lisaann@ya.ru")
                .login("gape")
                .name("Lisa")
                .birthday(LocalDate.of(1972, 5, 9))
                .build();

        userDbStorage.createUser(user1);

        User user2 = User.builder()
                .id(2L)
                .email("lindalovelace@ya.ru")
                .login("ll")
                .name("Linda")
                .birthday(LocalDate.of(1949, 1, 10))
                .build();

        userDbStorage.createUser(user2);

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
        filmDbStorage.createFilm(film);

        Optional<Like> optionalLike = Optional.of(likeDbStorage.createLike(1L, 2L));

        assertThat(optionalLike)
                .isPresent()
                .hasValueSatisfying(like ->
                        assertThat(like).hasFieldOrPropertyWithValue("filmId", 1L)
                                .hasFieldOrPropertyWithValue("userId", 2L)
                );
    }
}
