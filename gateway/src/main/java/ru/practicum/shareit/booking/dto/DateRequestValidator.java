package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface DateRequestValidator {
    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
}
