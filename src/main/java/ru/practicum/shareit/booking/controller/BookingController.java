package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse addBooking(@RequestBody @Valid BookingRequest bookingRequest,
                                      @RequestHeader("X-Sharer-User-Id") Long bookerId)
            throws NotFoundException, ItemException {
        return bookingService.addBooking(bookingRequest, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) throws NotFoundException, ItemException {
        return bookingService.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved)
            throws ItemException, NotFoundException, StateException {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    private List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state)
            throws NotFoundException, ItemException, StateException {
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    private List<BookingResponse> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state)
            throws NotFoundException, ItemException, StateException {
        return bookingService.getAllOwnerBookings(userId, state);
    }
}
