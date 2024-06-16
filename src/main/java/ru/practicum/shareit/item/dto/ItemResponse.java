package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponse {
    Long id;
    String name;
    String description;
    @JsonProperty("available")
    Boolean isAvailable;
    Long userId;
    ItemBookingResponse lastBooking;
    ItemBookingResponse nextBooking;
    List<CommentResponse> comments;
    Long requestId;
}
