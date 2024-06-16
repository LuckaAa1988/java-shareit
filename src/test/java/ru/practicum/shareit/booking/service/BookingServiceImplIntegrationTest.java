package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeAll
    void setUp() {
        owner = createUser("Owner", "owner@example.com");
        booker = createUser("Booker", "booker@example.com");
        item = createItem("Drill", "Powerful drill", owner);
        booking = createBooking(booker, item, Status.APPROVED, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        userRepository.saveAndFlush(owner);
        userRepository.saveAndFlush(booker);
        itemRepository.saveAndFlush(item);
        bookingRepository.saveAndFlush(booking);
    }

    @Test
    void testGetAllOwnerBookings() throws NotFoundException, StateException {
        List<BookingResponse> bookings = bookingService.getAllOwnerBookings(owner.getId(), "ALL", 0, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllOwnerBookingsWithState() throws NotFoundException, StateException {
        List<BookingResponse> bookings = bookingService.getAllOwnerBookings(owner.getId(), "ALL", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void testGetAllOwnerBookingsNotFound() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            bookingService.getAllOwnerBookings(999L, "ALL", 0, 10);
        });

        assertEquals("USER с id 999 не существует", thrown.getMessage());
    }

    @Test
    void testGetAllOwnerBookingsStateException() {
        StateException thrown = assertThrows(StateException.class, () -> {
            bookingService.getAllOwnerBookings(owner.getId(), "INVALID_STATE", 0, 10);
        });

        assertEquals("Unknown state: INVALID_STATE", thrown.getMessage());
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private Item createItem(String name, String description, User owner) {
        return Item.builder()
                .name(name)
                .description(description)
                .user(owner)
                .isAvailable(true)
                .build();
    }

    private Booking createBooking(User booker, Item item, Status status, LocalDateTime startDate, LocalDateTime endDate) {
        return Booking.builder()
                .booker(booker)
                .item(item)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
