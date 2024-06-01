package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserId(Long userId);

    @Query(value = "SELECT i FROM Item i " +
            "WHERE lower(i.name) LIKE %:text% OR lower(i.description) LIKE %:text% AND i.isAvailable = true")
    List<Item> searchItems(@Param("text") String text);
}
