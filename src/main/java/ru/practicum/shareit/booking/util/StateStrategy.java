package ru.practicum.shareit.booking.util;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.model.StateException;

import java.util.List;

public interface StateStrategy {
    List<Booking> findBookings(Long bookerId, Integer from, Integer size) throws StateException;

    List<Booking> findBookingsByItemIds(List<Long> itemIds, Integer from, Integer size) throws StateException;

    State getState();
}
