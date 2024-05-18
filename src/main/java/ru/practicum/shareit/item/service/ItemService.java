package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException;

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) throws NotFoundException, AccessDeniedException;

    ItemDto getItem(Long userId, Long itemId) throws NotFoundException;

    List<ItemDto> getAllItems(Long userId) throws NotFoundException;

    List<ItemDto> searchItems(String text, Long userId) throws NotFoundException;
}
