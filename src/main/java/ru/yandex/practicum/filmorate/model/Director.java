package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private long id;
    private final String name;
    private final String surname;
}
