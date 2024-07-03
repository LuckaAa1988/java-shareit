package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponse addRequestItem(@RequestBody ItemRequestRequest itemRequestRequest,
                                              @RequestHeader("X-Sharer-User-Id") Long authorId)
            throws NotFoundException {
        return itemRequestService.addRequestItem(itemRequestRequest, authorId);
    }

    @GetMapping
    public List<ItemRequestResponse> getAllOwnerRequestItem(@RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            @RequestHeader("X-Sharer-User-Id") Long authorId)
            throws NotFoundException, StateException {
        return itemRequestService.getAllOwnerRequestItem(from, size, authorId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequestItem(@RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestHeader("X-Sharer-User-Id") Long authorId)
            throws NotFoundException, StateException {
        return itemRequestService.getAllRequestItem(from, size, authorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequestItem(@PathVariable Long requestId,
                                              @RequestHeader("X-Sharer-User-Id") Long authorId) throws NotFoundException {
        return itemRequestService.getRequestItem(requestId, authorId);
    }

}
