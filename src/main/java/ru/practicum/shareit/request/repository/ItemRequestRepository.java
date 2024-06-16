package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = "SELECT * FROM item_requests WHERE author_id =:authorId LIMIT :size OFFSET:from", nativeQuery = true)
    List<ItemRequest> findAllByAuthor(Long authorId, Integer from, Integer size);

    @Query(value = "SELECT * FROM item_requests LIMIT :size OFFSET :from", nativeQuery = true)
    List<ItemRequest> findAll(Integer from, Integer size);
}
