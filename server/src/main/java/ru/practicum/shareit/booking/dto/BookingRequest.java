package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {
    Long itemId;
    @JsonProperty("start")
    LocalDateTime startDate;
    @JsonProperty("end")
    LocalDateTime endDate;
    Long bookerId;
}
