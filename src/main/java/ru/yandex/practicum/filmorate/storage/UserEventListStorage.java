package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.List;

public interface UserEventListStorage {
    List<UserEvent> getListById(long userId);
    void addEvent(long userId , String eventType, String operation, long entityId);
}
