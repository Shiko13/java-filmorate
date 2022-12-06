package ru.yandex.practicum.filmorate.util;

import javax.validation.Payload;

public @interface ReleaseDateValidation {
    String message() default "Lumiere brothers look at you with surprise! (to much early date)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
