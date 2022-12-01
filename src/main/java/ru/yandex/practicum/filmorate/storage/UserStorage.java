package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    List<User> findAll();
    User findById(long userId);
    User create(User user);
    User update(User user);
    void deleteAll();
    void deleteById(long userId);
}
