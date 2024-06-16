package ru.practicum.shareit.booking.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.StateException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StateAll implements StateStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookings(Long bookerId, Integer from, Integer size) throws StateException {
        if (size != null && size == 0) {
            throw new StateException("размер не может быть 0");
        }
        return bookingRepository.findAll(bookerId, from == null ? 0 : from, size == null ? Integer.MAX_VALUE : size);
    }

    @Override
    public List<Booking> findBookingsByItemIds(List<Long> itemIds, Integer from, Integer size) throws StateException {
        if (size != null && size == 0) {
            throw new StateException("размер не может быть 0");
        }
        return bookingRepository.findAll(itemIds, from == null ? 0 : from, size == null ? Integer.MAX_VALUE : size);
    }

    @Override
    public State getState() {
        return State.ALL;
    }
}
