package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse addRequestItem(ItemRequestRequest itemRequestRequest, Long authorId) throws NotFoundException;

    List<ItemRequestResponse> getAllRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException;

    List<ItemRequestResponse> getAllOwnerRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException;

    ItemRequestResponse getRequestItem(Long requestId, Long authorId) throws NotFoundException;
}
