package ru.yandex.practicum.filmorate.model.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EntityIdExistValidator implements ConstraintValidator<EntityIdExistValidation, Long> {
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return id != 0;
    }
}
