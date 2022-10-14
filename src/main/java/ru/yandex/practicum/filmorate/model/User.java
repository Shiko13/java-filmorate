package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @Email @NotNull
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent

    private LocalDate birthday;

    public void setName(String name) {
        this.name = name;
    }
}