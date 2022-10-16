package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User changeUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }

    @Override
    public boolean isContainUser(long id) {
        return users.containsKey(id);
    }
}
