package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void fieldsFilmShouldBeCorrect() {
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

        Optional<Film> optionalFilm = Optional.of(filmDbStorage.createFilm(film));

        assertThat(optionalFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Hot in Las Vegas")
                );

        Optional<Film> optionalFilmById = Optional.of(filmDbStorage.findFilmById(1L));

        assertThat(optionalFilmById)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Hot in Las Vegas")
                );

        Film updateFilm = Film.builder()
                .id(1)
                .name("Hot-2 in sea: Stagnetti's Revenge")
                .description("Casino and some problems")
                .releaseDate(LocalDate.of(1994, 10, 10))
                .duration(69)
                .mpa(mpa)
                .build();

        Optional<Film> testUpdateFilm = Optional.of(filmDbStorage.createFilm(updateFilm));

        assertThat(testUpdateFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Hot-2 in sea: Stagnetti's Revenge")
                );
    }
}
