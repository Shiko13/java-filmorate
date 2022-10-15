package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserService {
    Collection<User> findAllUsers();
    User findUserById(long id);
    User createUser(User user);
    User changeUser(User user);
    void deleteAllUsers();
    void deleteUserById(long id);
    void addToFriends(long userId, long friendId);
    void deleteFromFriends(long userId, long friendId);
    Collection<User> findAllFriends(long id);
    Collection<User> findCommonFriends(long userId, long anotherUserId);
    void validationUser(User user);
    void checkUser(long userId, long friendId);

}
