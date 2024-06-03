package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface StateStrategy {
    List<Booking> findBookings(Long bookerId);

    List<Booking> findBookingsByItemIds(List<Long> itemIds);

    State getState();
}
