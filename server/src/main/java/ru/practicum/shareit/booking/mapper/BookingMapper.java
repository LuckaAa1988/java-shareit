package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.item.dto.ItemBookingResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "booker.userId",
            expression = "java(user.getId())")
    @Mapping(target = "item.bookingItemId",
            expression = "java(item.getId())")
    @Mapping(target = "id", expression = "java(booking.getId())")
    @Mapping(target = "status",
            expression = "java(ru.practicum.shareit.booking.util.Status.valueOf(booking.getStatus().name()))")
    BookingResponse toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking fromDto(BookingRequest bookingRequest, User booker, Item item, Status status);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    ItemBookingResponse toDtoItemBooking(Booking booking);
}
