package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
    UserController userController = new UserController();
    FilmController filmController = new FilmController();

    @Test
    void contextLoads() {
    }

    @Test
    void ifUsernameIsNotIndicateItShouldBeBecomeEqualsLogin() {
        User user = new User(1, "s.al.terentev@gmail.com", "Pistoletov", "", LocalDate.parse("2000-11-11"));
        userController.createUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void releaseDateShouldBeAfter28december1895() {
        Film film = new Film(1, "Leon", "About killer and girl", LocalDate.parse("1894-09-14"), 110);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> filmController.createFilm(film)
        );
        assertEquals("Братья Люмьер смотрят на вас с недоумением! (кажется, вы ошиблись с датой", exception.getMessage());
    }

//    @Test
//    void emailShouldNotBeEmpty() {
//        User user = new User(1, "", "login1", "god_of_sea", LocalDate.parse("2000-11-11"));
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> userController.createUser(user)
//        );
//        assertEquals("Собака — лучший друг человека, проверьте, что в почте есть @", exception.getMessage());
//    }
//
//    @Test
//    void emailShouldBeContainAtSign() {
//        User user = new User(1, "s.al.terentevgmail.com", "login1", "", LocalDate.parse("2000-11-11"));
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> userController.createUser(user)
//        );
//        assertEquals("Собака — лучший друг человека, проверьте, что в почте есть @", exception.getMessage());
//    }
//
//    @Test
//    void loginShouldNotBeEmpty() {
//        User user = new User(1, "s.al.terentev@gmail.com", "", "doggy_style", LocalDate.parse("2000-11-11"));
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> userController.createUser(user)
//        );
//        assertEquals("Пробелы — не друг логина, проверьте, что в логине нет пробелов", exception.getMessage());
//    }
//
//    @Test
//    void loginShouldNotBeContainSpace() {
//        User user = new User(1, "s.al.terentev@gmail.com", "Crazy Parrot", "crazyParrot", LocalDate.parse("2000-11-11"));
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> userController.createUser(user)
//        );
//        assertEquals("Пробелы — не друг логина, проверьте, что в логине нет пробелов", exception.getMessage());
//    }
//
//
//
//    @Test
//    void birthdayShouldNotBeAfterNow() {
//        User user = new User(1, "s.al.terentev@gmail.com", "honeymoon", "", LocalDate.parse("2025-11-11"));
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> userController.createUser(user)
//        );
//        assertEquals("Даже Бенджамину Баттону такое не под силу! Проверьте, что дата рождения указана верно :)", exception.getMessage());
//    }
//
//    @Test
//    void nameOfFilmShouldNotBeEmpty() {
//        Film film = new Film(1, "", "About killer and girl", LocalDate.parse("1994-09-14"), 110);
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> filmController.createFilm(film)
//        );
//        assertEquals("Лучше всё-таки дать фильму какое-то название :)", exception.getMessage());
//    }
//
//    @Test
//    void descriptionShouldNotBeOver200Symbols() {
//        Film film = new Film(1, "Leon",
//                "Léon is an Italian hitman (or \"cleaner\", as he refers to himself) in the New York City " +
//                        "neighborhood of Little Italy working for a mafioso named \"Old Tony\". One day, Léon meets " +
//                        "Mathilda Lando, a lonely twelve-year-old who lives with her",
//                LocalDate.parse("1994-09-14"), 110);
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> filmController.createFilm(film)
//        );
//        assertEquals("Придётся сократить описание фильма до 200 символов :(", exception.getMessage());
//    }
//
//
//
//    @Test
//    void durationShouldNotBeNegative() {
//        Film film = new Film(1, "Leon", "About killer and girl", LocalDate.parse("1994-09-14"), -110);
//        final ValidateException exception = assertThrows(
//                ValidateException.class,
//                () -> filmController.createFilm(film)
//        );
//        assertEquals("Даже Нолан пока не умеет снимать фильмы с отрицательной продолжительностью :)", exception.getMessage());
//    }
}
