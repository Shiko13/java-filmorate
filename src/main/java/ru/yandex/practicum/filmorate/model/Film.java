package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Film {
    private long id;
    @NotBlank
    private final String name;
    @Size(max = 200) @NotNull
    private final String description;
    @NotNull @ReleaseDateValidation
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    @NotNull
    private final Mpa mpa;
    @Builder.Default
    private List<Director> directors = new ArrayList<>();
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
}
