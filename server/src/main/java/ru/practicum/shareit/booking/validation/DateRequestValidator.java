package ru.practicum.shareit.booking.validation;

import java.time.LocalDateTime;

public interface DateRequestValidator {
    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
