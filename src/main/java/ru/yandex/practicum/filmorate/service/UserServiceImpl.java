package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDb") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public List<User> getAll() {
        log.debug("Start request GET to /users");

        return userStorage.findAllUsers();
    }

    @Override
    public User getById(long id) {
        log.debug("Start request GET to /users/{}", id);

        return userStorage.findUserById(id);
    }

    @Override
    public List<User> getCommonFriends(long userId, long anotherUserId) {
        log.debug("Start request GET to /users/{}/friends/common/{}", userId, anotherUserId);

        return friendshipStorage.readCommonFriends(userId, anotherUserId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        log.debug("Start request GET to /users/{}/friends", id);

        return friendshipStorage.readAllFriends(id);
    }

    @Override
    public User create(User user) {
        log.debug("Start request POST to /users, with id = {}, email = {}, login = {}, name = {}, birthday = {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        throwIfNotValid(user);
        return userStorage.createUser(user);
    }

    @Override
    public User update(User user) {
        log.debug("Start request PUT to /users, with id = {}, email = {}, login = {}, name = {}, birthday = {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return userStorage.updateUser(user);
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        log.debug("Start request PUT to /users/{}/friends/{}", userId, friendId);

        if (userId <= 0 || friendId <= 0) {
            throw new NotFoundException("Id should be positive");
        }

        friendshipStorage.createFriend(userId, friendId);
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

        friendshipStorage.deleteFromFriends(userId, friendId);
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