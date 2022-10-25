package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private long counterId = 1;
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAll() {
        log.debug("Start request GET to /users)");

        return userStorage.findAllUsers();
    }

    @Override
    public User getById(long id) {
        log.debug("Start request GET to /users/{}", id);

        return userStorage.findUserById(id)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + id + " not found"));
    }

    @Override
    public List<User> findCommonFriends(long userId, long anotherUserId) {
        log.debug("Start request GET to /users/{}/friends/common/{}", userId, anotherUserId);

        User user = userStorage
                .findUserById(userId).orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        User anotherUser = userStorage.findUserById(anotherUserId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + anotherUserId + " not found"));

        List<User> commonFriends = new ArrayList<>();

        for (long friend : user.getFriends()) {
            if (anotherUser.getFriends().contains(friend)) {
                userStorage.findUserById(friend)
                        .ifPresent(commonFriends::add);
            }
        }

        return commonFriends;
    }

    @Override
    public List<User> findAllFriends(long id) {
        log.debug("Start request GET to /users/{}/friends)", id);

        User user = userStorage
                .findUserById(id).orElseThrow(() ->
                        new NotFoundException("User with id = " + id + " not found"));
        List<User> friends = new ArrayList<>();

        for (long friend : user.getFriends()) {
            userStorage.findUserById(friend).ifPresent(friends::add);
        }

        return friends;
    }

    @Override
    public User create(User user) {
        log.debug("Start request POST to /users)");

        if (userStorage.isExist(user.getId())) {
            log.warn("Unsuccessful request POST to /users, attempt to add existing user");
            throw new ValidateException("Filmorate already contains this user");
        }
        throwIfNotValid(user);

        user.setId(counterId++);
        log.info("Process of adding new user");

        return userStorage.createUser(user);
    }

    @Override
    public User update(User user) {
        log.debug("Start request PUT to /users)");

        userStorage.findUserById(user.getId())
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + user.getId() + " not found"));

        throwIfNotValid(user);
        log.info("Process of updating the user");

        return userStorage.changeUser(user);
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        log.debug("Start request PUT to /users/{}/friends/{}", userId, friendId);

        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + friendId + " not found"));

        user.addToFriends(friendId);
        friend.addToFriends(userId);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /users)");
        userStorage.deleteAllUsers();
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /users/{}", id);
        userStorage.deleteUserById(id);
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        log.debug("Start request DELETE to /users/{}/friends/{}", userId, friendId);

        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " not found"));
        userStorage.findUserById(friendId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + friendId + " not found"));

        user.deleteFromFriends(friendId);
    }

    @Override
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