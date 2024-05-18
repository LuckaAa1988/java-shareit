package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    ItemDto createItem(Item item);

    Item finById(Long itemId) throws NotFoundException;

    ItemDto updateItem(Item item, Long itemId) throws NotFoundException;

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}
