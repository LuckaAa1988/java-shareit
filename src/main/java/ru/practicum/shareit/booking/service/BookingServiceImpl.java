package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.util.Constants;
import ru.practicum.shareit.booking.util.State;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.repository.BookingSpecification.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponse addBooking(BookingRequest bookingRequest, Long bookerId)
            throws NotFoundException, ItemException {
        if (notExists(bookerId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, bookerId));
        }
        var item = getItem(bookingRequest.getItemId());
        if (!item.getIsAvailable()) {
            throw new ItemException(String.format("Item с id %s не доступен.", bookingRequest.getItemId()));
        }
        if (bookerId.equals(item.getUser().getId())) {
            throw new NotFoundException("Нельзя взять в аренду свою вещь.");
        }
        var booker = userRepository.findById(bookerId).get();
        var booking = bookingRepository.saveAndFlush(
                BookingMapper.INSTANCE.fromDto(bookingRequest, booker, item, Status.WAITING));
        return BookingMapper.INSTANCE.toDto(booking);
    }

    @Override
    public BookingResponse getBooking(Long userId, Long bookingId) throws NotFoundException {
        var booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.BOOKING_NOT_FOUND, bookingId)));
        var itemUserId = booking.getItem().getUser().getId();
        if (!booking.getBooker().getId().equals(userId) && !itemUserId.equals(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        return BookingMapper.INSTANCE.toDto(booking);
    }

    @Override
    public BookingResponse updateBookingStatus(Long userId, Long bookingId, Boolean approved)
            throws NotFoundException, StateException {
        var booking = getBooking(userId, bookingId);
        var item = getItem(booking.getItem().getBookingItemId());
        if (!item.getUser().getId().equals(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new StateException("Статус уже изменен на " + Status.APPROVED.name());
        }
        if (approved) {
            bookingRepository.updateBookingStatus(bookingId, Status.APPROVED);
            item.setIsAvailable(false);
            booking.setStatus(Status.APPROVED);
        } else {
            bookingRepository.updateBookingStatus(bookingId, Status.REJECTED);
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    @Override
    public List<BookingResponse> getAllUserBookings(Long userId, String state)
            throws NotFoundException, StateException {
        if (notExists(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        List<Booking> bookings;
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + state);
        }
        switch (State.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAll(byBookerId(userId));
                break;
            case FUTURE:
                bookings = bookingRepository.findAll(byBookerId(userId).and(startDateIsAfter()));
                break;
            case PAST:
                bookings = bookingRepository.findAll(byBookerId(userId).and(endDateIsBefore()));
                break;
            case CURRENT:
                bookings = bookingRepository.findAll(byBookerId(userId).and(startDateIsBefore()).and(endDateIsAfter()));
                break;
            case WAITING:
                bookings = bookingRepository.findAll(byBookerId(userId).and(byStatus(Status.WAITING)));
                break;
            case REJECTED:
                bookings = bookingRepository.findAll(byBookerId(userId).and(byStatus(Status.REJECTED)));
                break;
            default:
                throw new StateException("Unknown state: " + state);
        }
        return getBookingResponses(bookings);
    }

    @Override
    public List<BookingResponse> getAllOwnerBookings(Long userId, String state)
            throws NotFoundException, StateException {
        if (notExists(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        var itemIds = itemRepository.findAllByUserId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings;
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + state);
        }
        switch (State.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAll(byItemIds(itemIds));
                break;
            case FUTURE:
                bookings = bookingRepository.findAll(byItemIds(itemIds).and(startDateIsAfter()));
                break;
            case PAST:
                bookings = bookingRepository.findAll(byItemIds(itemIds).and(endDateIsBefore()));
                break;
            case CURRENT:
                bookings = bookingRepository.findAll(byItemIds(itemIds).and(startDateIsBefore()).and(endDateIsAfter()));
                break;
            case WAITING:
                bookings = bookingRepository.findAll(byItemIds(itemIds).and(byStatus(Status.WAITING)));
                break;
            case REJECTED:
                bookings = bookingRepository.findAll(byItemIds(itemIds).and(byStatus(Status.REJECTED)));
                break;
            default: throw new StateException("Unknown state: " + state);
        }
        return getBookingResponses(bookings);
    }

    private List<BookingResponse> getBookingResponses(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    private boolean notExists(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }

    private Item getItem(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ITEM_NOT_FOUND, itemId)));
    }
}
