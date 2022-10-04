package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос к эндпоинту /users, метод GET");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту /users, метод POST");
        if (!users.containsValue(user)) {
            validationUser(user);
        } else {
            throw new ValidateException("Пользователь с такой почтой уже зарегистрирован :)");
        }
        user.setId(id++);
        log.info("Добавление нового пользователя");
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User changeUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту /users, метод PUT");
        if (!users.containsKey(user.getId())) {
            throw new ValidateException("Пользователь с id " + user.getId() + " не найден");
        }
        validationUser(user);
        log.info("Обновление пользователя");
        users.put(user.getId(), user);
        return user;
    }

    public void validationUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidateException("Логин не должен содержать пробелов");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Происходит изменение отображаемого имени на электронную почту");
            user.setName(user.getLogin());
        }
        log.info("Валидация пройдена");
    }
}
