package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
    private final Long entityId;
    private final Long userId;
    private final Boolean isPositive;
}
