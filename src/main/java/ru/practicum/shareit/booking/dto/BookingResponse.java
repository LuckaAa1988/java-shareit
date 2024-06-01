package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.util.Status;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    @JsonProperty("id")
    Long id;
    @JsonProperty("start")
    LocalDateTime startDate;
    @JsonProperty("end")
    LocalDateTime endDate;
    Status status;
    BookerResponse booker;
    BookingItemResponse item;
}
