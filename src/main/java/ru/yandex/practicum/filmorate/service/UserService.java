package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService extends Crud<User> {
    void addToFriends(long userId, long friendId);
    void deleteFromFriends(long userId, long friendId);
    List<User> findAllFriends(long id);
    List<User> findCommonFriends(long userId, long anotherUserId);
    void validationUser(User user);
    void checkUser(long userId, long friendId);

}
