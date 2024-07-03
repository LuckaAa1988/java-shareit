package ru.practicum.shareit.booking.util;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.model.StateException;

import java.util.List;

public interface StateStrategy {
    List<Booking> findBookings(Long bookerId, Pageable pageable) throws StateException;

    List<Booking> findBookingsByItemIds(List<Long> itemIds, Pageable pageable) throws StateException;

    State getState();
}
