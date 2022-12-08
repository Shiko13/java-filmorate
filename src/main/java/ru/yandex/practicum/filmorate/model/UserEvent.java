package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEvent {
    private final Long timestamp;
    private final Long userId;
    private final String eventType;
    private final String operation;
    private final Long eventId;
    private final Long entityId;
}
