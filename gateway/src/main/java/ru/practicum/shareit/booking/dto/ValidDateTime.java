package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
@Documented
public @interface ValidDateTime {
    String message() default "Ошибка в датах аренды";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
