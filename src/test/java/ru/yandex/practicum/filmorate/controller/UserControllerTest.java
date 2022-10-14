package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    UserController userController;

    @Test
    void ifUsernameIsNotIndicateItShouldBeBecomeEqualsLogin() {
        User user = new User(1, "s.al.terentev@gmail.com", "Pistoletov", "", LocalDate.parse("2000-11-11"));
        userController.createUser(user);

        assertEquals(user.getName(), user.getLogin());
    }
}