package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;

import java.util.List;

public interface BookingService {
    BookingResponse addBooking(BookingRequest bookingRequest, Long bookerId) throws NotFoundException, ItemException;

    BookingResponse getBooking(Long userId, Long bookingId) throws NotFoundException, ItemException;

    BookingResponse updateBookingStatus(Long userId, Long bookingId, Boolean approved)
            throws NotFoundException, ItemException, StateException;

    List<BookingResponse> getAllUserBookings(Long userId, String state, Integer from, Integer size)
            throws NotFoundException, ItemException, StateException;

    List<BookingResponse> getAllOwnerBookings(Long userId, String state, Integer from, Integer size)
            throws NotFoundException, ItemException, StateException;
}
