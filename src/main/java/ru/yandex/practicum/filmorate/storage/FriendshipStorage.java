package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface FriendshipStorage {
    List<User> readAll(long userId);
    Friendship create(long userOneId, long userTwoId);
    void delete(long userOneId, long userTwoId);
    List<User> readCommon(long userOneId, long userTwoId);
}
