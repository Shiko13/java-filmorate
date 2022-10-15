package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController filmController;

    @Test
    void releaseDateShouldBeAfter28december1895() {
        Film film = new Film(1, "Leon", "About killer and girl", LocalDate.parse("1894-09-14"), 110);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> filmController.createFilm(film)
        );
        assertEquals("To much early date", exception.getMessage());
    }
}