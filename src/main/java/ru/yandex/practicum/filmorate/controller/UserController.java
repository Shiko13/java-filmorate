package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable long id,
                                              @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable long id) {
        return userService.getRecommendations(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        throwIfNotValid(user);

        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable long id,
                             @PathVariable long friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        userService.deleteById(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable long id,
                                  @PathVariable long friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    public void throwIfNotValid(User user) {
        log.debug("Start validation of user");

        if (user.getLogin().contains(" ")) {
            log.warn("Unsuccessful validation of user");
            throw new ValidateException("Login should not contains a space");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Process of changing empty name to email");
            user.setName(user.getLogin());
        }
        log.info("Validation successful passed");
    }
}