package ru.yandex.practicum.filmorate.model.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EntityIdExistValidator.class)
public @interface EntityIdExistValidation {
    String message() default "Поля filmId и userId должны быть заполнены";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
