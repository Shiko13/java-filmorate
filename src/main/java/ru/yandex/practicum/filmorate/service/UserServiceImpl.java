package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserEventListStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final UserEventListStorage userEventListStorage;

    @Override
    public List<User> getAll() {
        log.debug("Start request GET to /users");

        return userStorage.findAll();
    }

    @Override
    public User getById(long id) {
        log.debug("Start request GET to /users/{}", id);

        return userStorage.findById(id);
    }

    @Override
    public List<User> getCommonFriends(long userId, long anotherUserId) {
        log.debug("Start request GET to /users/{}/friends/common/{}", userId, anotherUserId);

        return friendshipStorage.readCommon(userId, anotherUserId);
    }

    @Override
    public List<UserEvent> getEventListByUserId(long id) {
        log.debug("Start request GET to /users/{}/feed", id);

        return userEventListStorage.getListById(id);
    }

    @Override
    public List<User> getAllFriends(long id) {
        log.debug("Start request GET to /users/{}/friends", id);

        return friendshipStorage.readAll(id);
    }

    @Override
    public User create(User user) {
        log.debug("Start request POST to /users, with id = {}, email = {}, login = {}, name = {}, birthday = {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        log.debug("Start request PUT to /users, with id = {}, email = {}, login = {}, name = {}, birthday = {}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        return userStorage.update(user);
    }

    @Override
    public void addToFriends(long userId, long friendId) {
        log.debug("Start request PUT to /users/{}/friends/{}", userId, friendId);

        if (userId <= 0 || friendId <= 0) {
            throw new NotFoundException("Id should be positive");
        }

        friendshipStorage.create(userId, friendId);

        userEventListStorage.addEvent(userId, "FRIEND", "ADD", friendId);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /users)");

        userStorage.deleteAll();
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /users/{}", id);

        userStorage.deleteById(id);
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        log.debug("Start request DELETE to /users/{}/friends/{}", userId, friendId);

        friendshipStorage.delete(userId, friendId);

        userEventListStorage.addEvent(userId, "FRIEND", "REMOVE", friendId);
    }
}