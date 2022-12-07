package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService extends Crud<User> {
    void addToFriends(long userId, long friendId);
    void deleteFromFriends(long userId, long friendId);
    List<User> getAllFriends(long id);
    List<User> getCommonFriends(long userId, long anotherUserId);
    List<Film> getRecommendations(long id);
}
