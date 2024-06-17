package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookerResponse;
import ru.practicum.shareit.booking.dto.BookingItemResponse;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
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

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StateFactory stateFactory;

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
        MockitoAnnotations.openMocks(this);

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
        bookerResponse.setUserId(3L);

        BookingItemResponse bookingItemResponse = new BookingItemResponse();
        bookingItemResponse.setBookingItemId(3L);

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
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.addBooking(bookingRequest, booker.getId());

        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
    }

    @Test
    void testGetBooking() throws Exception {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

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
        when(strategy.findBookings(anyLong(), anyInt(), anyInt())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item));

        List<BookingResponse> responses = bookingService.getAllUserBookings(1L, "ALL", 0, 10);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
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
        when(strategy.findBookingsByItemIds(anyList(), anyInt(), anyInt())).thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item));

        List<BookingResponse> responses = bookingService.getAllOwnerBookings(1L, "ALL", 0, 10);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }
}
