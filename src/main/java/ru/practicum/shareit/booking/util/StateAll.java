package ru.practicum.shareit.booking.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.StateException;

import java.util.List;

import static ru.practicum.shareit.booking.repository.BookingSpecification.byBookerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.byItemIds;

@Component
@RequiredArgsConstructor
public class StateAll implements StateStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookings(Long bookerId, Pageable pageable) throws StateException {
        return bookingRepository.findAll(byBookerId(bookerId), pageable).getContent();
    }

    @Override
    public List<Booking> findBookingsByItemIds(List<Long> itemIds, Pageable pageable) throws StateException {
        return bookingRepository.findAll(byItemIds(itemIds), pageable).getContent();
    }

    @Override
    public State getState() {
        return State.ALL;
    }
}
