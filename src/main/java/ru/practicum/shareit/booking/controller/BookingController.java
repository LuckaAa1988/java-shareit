package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse addBooking(@RequestBody @Valid BookingRequest bookingRequest,
                                      @RequestHeader("X-Sharer-User-Id") Long bookerId)
            throws NotFoundException, ItemException {
        log.info("Получен POST запрос на бронирование Item с id {} от USER c id {}",
                bookingRequest.getItemId(), bookerId);
        return bookingService.addBooking(bookingRequest, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) throws NotFoundException, ItemException {
        log.info("Получен GET запрос на просмотр бронирования с id {} от USER c id {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved)
            throws ItemException, NotFoundException, StateException {
        log.info("Получен PATCH запрос от USER c id {} по броинированию с id {} со статусом {}",
                userId, bookingId, approved);
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    private List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state)
            throws NotFoundException, ItemException, StateException {
        log.info("Получен GET запрос на просмотр всех бронирований USER c id {}, с параметром {}",
                userId, state);
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    private List<BookingResponse> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state)
            throws NotFoundException, ItemException, StateException {
        log.info("Получен GET запрос на просмотр всех бронирований USER c id {}, с параметром {}",
                userId, state);
        return bookingService.getAllOwnerBookings(userId, state);
    }
}
