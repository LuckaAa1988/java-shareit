package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.State;
import ru.practicum.shareit.booking.util.StateFactory;
import ru.practicum.shareit.booking.util.StateStrategy;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.exception.util.Constants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final StateFactory stateFactory;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse addBooking(BookingRequest bookingRequest, Long bookerId)
            throws NotFoundException, ItemException {
        log.info("Бронирование Item с id {} от USER c id {}",
                bookingRequest.getItemId(), bookerId);
        if (notExists(bookerId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, bookerId));
        }
        var item = getItem(bookingRequest.getItemId());
        if (!item.getIsAvailable()) {
            throw new ItemException(String.format("Item с id %s недоступен.", bookingRequest.getItemId()));
        }
        if (bookerId.equals(item.getUser().getId())) {
            throw new NotFoundException("Нельзя взять в аренду свою вещь.");
        }
        var booker = userRepository.findById(bookerId).get();
        var booking = bookingRepository.saveAndFlush(bookingMapper.fromDto(bookingRequest, booker, item, Status.WAITING));
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponse getBooking(Long userId, Long bookingId) throws NotFoundException {
        log.info("Просмотр бронирования с id {} от USER c id {}", bookingId, userId);
        var booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.BOOKING_NOT_FOUND, bookingId)));
        var itemUserId = booking.getItem().getUser().getId();
        if (!booking.getBooker().getId().equals(userId) && !itemUserId.equals(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long userId, Long bookingId, Boolean approved)
            throws NotFoundException, StateException {
        log.info("USER c id {} обновляет броинирование с id {} со статусом {}",
                userId, bookingId, approved);
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
            booking.setStatus(Status.APPROVED);
        } else {
            bookingRepository.updateBookingStatus(bookingId, Status.REJECTED);
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    @Override
    public List<BookingResponse> getAllUserBookings(Long userId, String state, Integer from, Integer size)
            throws NotFoundException, StateException {
        log.info("Просмотр всех бронирований USER c id {}, с параметром {}",
                userId, state);
        if (from < 0) throw new RuntimeException("from не может быть меньше 0");
        if (notExists(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        List<Booking> bookings;
        try {
            StateStrategy strategy = stateFactory.findStrategy(State.valueOf(state));
            bookings = strategy.findBookings(userId, pageable);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + state);
        }
        return getBookingResponses(bookings);
    }

    @Override
    public List<BookingResponse> getAllOwnerBookings(Long userId, String state, Integer from, Integer size)
            throws NotFoundException, StateException {
        log.info("Просмотр всех бронирований USER c id {}, с параметром {}",
                userId, state);
        if (from < 0) throw new RuntimeException("from не может быть меньше 0");
        if (notExists(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        var itemIds = itemRepository.findAllByUserId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings;
        try {
            StateStrategy strategy = stateFactory.findStrategy(State.valueOf(state));
            bookings = strategy.findBookingsByItemIds(itemIds, pageable);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: " + state);
        }
        return getBookingResponses(bookings);
    }

    private List<BookingResponse> getBookingResponses(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    private boolean notExists(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }

    private Item getItem(Long itemId) throws NotFoundException {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ITEM_NOT_FOUND, itemId)));
    }
}
