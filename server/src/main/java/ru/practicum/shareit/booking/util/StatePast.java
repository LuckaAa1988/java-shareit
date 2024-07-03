package ru.practicum.shareit.booking.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

import static ru.practicum.shareit.booking.repository.BookingSpecification.*;

@Component
@RequiredArgsConstructor
public class StatePast implements StateStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookings(Long bookerId, Pageable pageable) {
        return bookingRepository.findAll(byBookerId(bookerId).and(endDateIsBefore()));
    }

    @Override
    public List<Booking> findBookingsByItemIds(List<Long> itemIds, Pageable pageable) {
        return bookingRepository.findAll(byItemIds(itemIds).and(endDateIsBefore()));
    }

    @Override
    public State getState() {
        return State.PAST;
    }
}
