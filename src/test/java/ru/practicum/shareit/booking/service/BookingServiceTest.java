package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingItemResponse;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StateFactory stateFactory;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User booker;
    private Item item;
    private Booking booking;
    private Booking updateBooking;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker User");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setIsAvailable(true);
        item.setUser(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        updateBooking = new Booking();
        updateBooking.setId(3L);
        updateBooking.setItem(item);
        updateBooking.setBooker(booker);
        updateBooking.setStatus(Status.WAITING);

        bookingRequest = new BookingRequest();
        bookingRequest.setItemId(item.getId());

        BookerResponse bookerResponse = new BookerResponse();
        bookerResponse.setUserId(2L);

        BookingItemResponse bookingItemResponse = new BookingItemResponse();
        bookingItemResponse.setBookingItemId(1L);

        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setItem(bookingItemResponse);
        bookingResponse.setBooker(bookerResponse);
        bookingResponse.setStatus(Status.WAITING);
    }

    @Test
    void testAddBookingUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(bookingRequest, 1L));

        assertEquals("USER с id 1 не существует", exception.getMessage());
    }

    @Test
    void testAddBookingItemNotFound() {
        item.setIsAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemException exception = assertThrows(ItemException.class, () ->
                bookingService.addBooking(bookingRequest, 1L));

        assertEquals("Item с id 1 недоступен.", exception.getMessage());
    }

    @Test
    void testAddBookingSelfItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(bookingRequest, 1L));

        assertEquals("Нельзя взять в аренду свою вещь.", exception.getMessage());
    }

    @Test
    void testAddBooking() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.fromDto(any(BookingRequest.class), any(), any(), any())).thenReturn(booking);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse response = bookingService.addBooking(bookingRequest, booker.getId());

        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
    }

    @Test
    void testGetBooking() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse response = bookingService.getBooking(1L, 1L);

        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
    }

    @Test
    void testNotFoundGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(1L, 1L));

        assertEquals("BOOKING с id 1 не существует", exception.getMessage());
    }

    @Test
    void testGetAllBookings() throws NotFoundException, StateException {
        StateStrategy strategy = mock(StateStrategy.class);
        when(stateFactory.findStrategy(any(State.class))).thenReturn(strategy);
        when(strategy.findBookings(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));


        List<BookingResponse> responses = bookingService.getAllUserBookings(1L, "ALL", 0, 10);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void testGetAllBookingsSize0() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ArithmeticException exception = assertThrows(ArithmeticException.class, () ->
                bookingService.getAllUserBookings(1L, "ALL", 0, 0));
    }

    @Test
    void testUserNotFoundGetAllBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getAllUserBookings(1L, "ALL", 0, 10));

        assertEquals("USER с id 1 не существует", exception.getMessage());
    }

    @Test
    void testGetAllOwnerBookings() throws Exception {
        StateStrategy strategy = mock(StateStrategy.class);
        when(stateFactory.findStrategy(any(State.class))).thenReturn(strategy);
        when(strategy.findBookingsByItemIds(anyList(), any(Pageable.class))).thenReturn(List.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<BookingResponse> responses = bookingService.getAllOwnerBookings(1L, "ALL", 0, 10);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateBookingStatusApproved() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingResponse response = bookingService.updateBookingStatus(1L, 1L, true);

        assertEquals(Status.APPROVED, response.getStatus());
    }

    @Test
    void testUpdateBookingStatusRejected() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingResponse response = bookingService.updateBookingStatus(1L, 1L, false);

        assertEquals(Status.REJECTED, response.getStatus());
    }

    @Test
    void testUpdateBookingStatusItemNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(1L, 1L, true));

        assertEquals("ITEM с id 1 не существует", exception.getMessage());
    }
}
