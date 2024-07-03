package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateTimeValidator implements ConstraintValidator<ValidDateTime, DateRequestValidator> {
    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DateRequestValidator value, ConstraintValidatorContext context) {
        var start = value.getStartDate();
        var end = value.getEndDate();
        if (start == null || end == null) return false;
        if (start.isAfter(end)) return false;
        return !start.isEqual(end);
    }
}
