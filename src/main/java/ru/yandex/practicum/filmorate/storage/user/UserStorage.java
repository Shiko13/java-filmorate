package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();
    User findUserById(long id);
    User createUser(User user);
    User changeUser(User user);
    void deleteAllUsers();
    void deleteUserById(long id);
    boolean isContainUser(long id);
}
