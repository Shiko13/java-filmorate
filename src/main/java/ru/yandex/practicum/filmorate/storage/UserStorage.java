package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();
    User findUserById(long userId);
    User createUser(User user);
    User updateUser(User user);
    void deleteAllUsers();
    void deleteUserById(long userId);
}
