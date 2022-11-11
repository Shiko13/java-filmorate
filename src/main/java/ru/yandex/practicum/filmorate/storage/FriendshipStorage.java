package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface FriendshipStorage {
    List<User> readAllFriends(long userId);
    Friendship createFriend(long userOneId, long userTwoId);
    void deleteFromFriends(long userOneId, long userTwoId);
    List<User> readCommonFriends(long userOneId, long userTwoId);
}
