package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.Status;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class BookingSpecification {

    public static Specification<Booking> byBookerId(Long bookerId) {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("startDate")));
            return cb.equal(root.get("booker"), bookerId);
        };
    }

    public static Specification<Booking> byItemIds(List<Long> itemIds) {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("startDate")));
            return root.get("item").in(itemIds);
        };
    }

    public static Specification<Booking> byItemId(Long itemId) {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("startDate")));
            return cb.equal(root.get("item"), itemId);
        };
    }

    public static Specification<Booking> orderByAsc() {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("startDate")));
            return cb.conjunction();
        };
    }

    public static Specification<Booking> startDateIsAfter() {
        return (root, query, cb) -> cb.greaterThan(root.get("startDate"), LocalDateTime.now());
    }

    public static Specification<Booking> endDateIsBefore() {
        return (root, query, cb) -> cb.lessThan(root.get("endDate"), LocalDateTime.now());
    }

    public static Specification<Booking> startDateIsBefore() {
        return (root, query, cb) -> cb.lessThan(root.get("startDate"), LocalDateTime.now());
    }

    public static Specification<Booking> endDateIsAfter() {
        return (root, query, cb) -> cb.greaterThan(root.get("endDate"), LocalDateTime.now());
    }

    public static Specification<Booking> byStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

}
