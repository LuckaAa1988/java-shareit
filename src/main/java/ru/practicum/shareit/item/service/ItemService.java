package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(ItemCreate itemCreate, Long userId) throws NotFoundException;

    ItemResponse updateItem(ItemCreate itemCreate, Long userId, Long itemId)
            throws NotFoundException, AccessDeniedException;

    ItemResponse getItem(Long userId, Long itemId) throws NotFoundException;

    List<ItemResponse> getAllItems(Long userId) throws NotFoundException;

    List<ItemResponse> searchItems(String text, Long userId) throws NotFoundException;

    CommentResponse addComment(CommentRequest commentRequest, Long userId, Long itemId) throws NotFoundException, ItemException;
}
