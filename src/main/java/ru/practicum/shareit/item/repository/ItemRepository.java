package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    ItemDto createItem(Item item);

    Optional<Item> findById(Long itemId) throws NotFoundException;

    ItemDto updateItem(Item item, Long itemId) throws NotFoundException;

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}
