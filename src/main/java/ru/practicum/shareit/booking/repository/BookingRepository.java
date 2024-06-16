package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.Status;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Modifying
    @Query(value = "UPDATE Booking b SET b.status = :status WHERE b.id = :bookingId")
    void updateBookingStatus(Long bookingId, Status status);

    @Query(value = "SELECT * FROM bookings WHERE booker_id = :bookerId ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findAll(Long bookerId, Integer from, Integer size);

    @Query(value = "SELECT * FROM bookings WHERE item_id IN(:itemIds) ORDER BY start_date DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Booking> findAll(List<Long> itemIds, Integer from, Integer size);
}
