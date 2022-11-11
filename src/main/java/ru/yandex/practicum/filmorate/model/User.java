package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private long id;
    @Email @NotNull
    private final String email;
    @NotBlank
    private final String login;
    @Setter
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
}