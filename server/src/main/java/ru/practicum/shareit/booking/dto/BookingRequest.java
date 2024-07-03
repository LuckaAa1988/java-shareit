package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validation.DateRequestValidator;
import ru.practicum.shareit.booking.validation.ValidDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidDateTime
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest implements DateRequestValidator {
    Long itemId;
    @JsonProperty("start")
    @Future
    @NotNull
    LocalDateTime startDate;
    @JsonProperty("end")
    @Future
    @NotNull
    LocalDateTime endDate;
    Long bookerId;
}
