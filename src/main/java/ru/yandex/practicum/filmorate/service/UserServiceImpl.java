package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

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
    public Collection<User> findAllUsers() {
        log.trace("Start request GET to /users)");
        return userStorage.findAllUsers();
    }

    @Override
    public User findUserById(long id) {
        log.trace("Start request GET to /users/{id})");
        if (!userStorage.isContainUser(id)) {
            log.error("Unsuccessful request GET to /users/{id}");
            throw new NoSuchUserException("User with id = " + id + " not found");
        }
        return userStorage.findUserById(id);
    }

    @Override
    public Collection<User> findCommonFriends(long userId, long anotherUserId) {
        log.trace("Start request GET to /users/{id}/friends/common/{otherId})");
        Collection<User> commonFriends = new ArrayList<>();
        for (long friend : userStorage.findUserById(userId).getFriends()) {
            if (userStorage.findUserById(anotherUserId).getFriends().contains(friend)) {
                commonFriends.add(userStorage.findUserById(friend));
            }
        }
        return commonFriends;
    }

    @Override
    public Collection<User> findAllFriends(long id) {
        log.trace("Start request GET to /users/{id}/friends)");
        Collection<User> friends = new ArrayList<>();
        for (long friend : userStorage.findUserById(id).getFriends()) {
            friends.add(userStorage.findUserById(friend));
        }
        return friends;
    }

    @Override
    public User createUser(User user) {
        log.trace("Start request POST to /users)");
        if (!userStorage.isContainUser(user.getId())) {
            validationUser(user);
        } else {
            log.error("Unsuccessful request POST to /users");
            throw new ValidateException("Filmorate already contains this user");
        }
        user.setId(counterId++);
        log.info("Process of adding new user");
        return userStorage.createUser(user);
    }

    @Override
    public User changeUser(User user) {
        log.trace("Start request PUT to /users)");
        if (!userStorage.isContainUser(user.getId())) {
            throw new NoSuchUserException("User with id = " + user.getId() + " not found");
        }
        validationUser(user);
        log.info("Process of updating the user");
        return userStorage.changeUser(user);
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        log.trace("Start request PUT to /users/{id}/friends/{friendId})");
        checkUser(userId, friendId);
        userStorage.findUserById(userId).addToFriends(friendId);
        userStorage.findUserById(friendId).addToFriends(userId);
    }

    @Override
    public void deleteAllUsers() {
        log.trace("Start request DELETE to /users)");
        userStorage.deleteAllUsers();
    }

    @Override
    public void deleteUserById(long id) {
        log.trace("Start request DELETE to /users/{id})");
        userStorage.deleteUserById(id);
    }

    public void checkUser(long userId, long friendId) {
        log.trace("Process of checking user");
        if (!userStorage.isContainUser(userId)) {
            throw new NoSuchUserException("User with id = " + userId + " not found");
        }
        if (!userStorage.isContainUser(friendId)) {
            throw new NoSuchUserException("User with id = " + friendId + " not found");
        }
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        log.trace("Start request DELETE to /users/{id}/friends/{friendId})");
        checkUser(userId, friendId);
        userStorage.findUserById(userId).deleteFromFriends(friendId);
    }

    @Override
    public void validationUser(User user) {
        log.trace("Start validation of user");
        if (user.getLogin().contains(" ")) {
            log.error("Unsuccessful validation of user");
            throw new ValidateException("Login should not contains a space");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Process of changing empty name to email");
            user.setName(user.getLogin());
        }
        log.info("Validation successful passed");
    }
}
