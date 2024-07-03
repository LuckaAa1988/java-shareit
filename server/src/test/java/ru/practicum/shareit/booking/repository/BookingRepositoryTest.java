package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.repository.BookingSpecification.byBookerId;
import static ru.practicum.shareit.booking.repository.BookingSpecification.byItemIds;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testUpdateBookingStatus() {
        User booker = createUser("John Doe", "john.doe@example.com");
        Item item = createItem(booker, "Drill", "Cordless drill", true);

        Booking booking = createBooking(booker, item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);
        bookingRepository.save(booking);
        entityManager.flush();

        bookingRepository.updateBookingStatus(booking.getId(), Status.APPROVED);
        entityManager.flush();
        entityManager.clear();

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testFindAllByBookerId() {
        User booker = createUser("Jane Doe", "jane.doe@example.com");
        Item item1 = createItem(booker, "Saw", "Hand saw", true);
        Item item2 = createItem(booker, "Hammer", "Heavy-duty hammer", true);

        Booking booking1 = createBooking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED);
        Booking booking2 = createBooking(booker, item2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), Status.REJECTED);
        bookingRepository.saveAll(List.of(booking1, booking2));
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findAll(byBookerId(booker.getId()));
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void testFindAllByItemIds() {
        User booker = createUser("Alice Smith", "alice.smith@example.com");
        Item item1 = createItem(booker, "Drill", "Cordless drill", true);
        Item item2 = createItem(booker, "Saw", "Electric saw", true);

        Booking booking1 = createBooking(booker, item1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED);
        Booking booking2 = createBooking(booker, item2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), Status.REJECTED);
        bookingRepository.saveAll(List.of(booking1, booking2));
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findAll(byItemIds(List.of(item1.getId(), item2.getId())));
        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    private User createUser(String name, String email) {
        User user = User.builder().name(name).email(email).build();
        entityManager.persist(user);
        return user;
    }

    private Item createItem(User user, String name, String description, boolean isAvailable) {
        Item item = Item.builder().name(name).description(description).isAvailable(isAvailable).user(user).build();
        entityManager.persist(item);
        return item;
    }

    private Booking createBooking(User booker, Item item, LocalDateTime startDate, LocalDateTime endDate, Status status) {
        Booking booking = Booking.builder().booker(booker).item(item).startDate(startDate).endDate(endDate).status(status).build();
        entityManager.persist(booking);
        return booking;
    }
}