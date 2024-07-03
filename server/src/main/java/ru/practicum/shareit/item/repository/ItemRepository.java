package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserId(Long userId, Pageable pageable);

    List<Item> findAllByUserId(Long userId);

    List<Item> findAllByItemRequestIdNotNull();

    List<Item> findAllByItemRequestAuthorId(Long authorId);

    @Query(value = "SELECT i FROM Item i " +
            "WHERE i.isAvailable = true AND (lower(i.name) LIKE %:text% OR lower(i.description) LIKE %:text%)")
    List<Item> searchItems(@Param("text") String text);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);
}
