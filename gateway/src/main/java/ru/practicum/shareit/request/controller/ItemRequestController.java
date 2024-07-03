package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequestItem(@RequestBody @Valid ItemRequestRequest itemRequestRequest,
                                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemRequestClient.addRequestItem(itemRequestRequest, authorId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerRequestItem(@RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size,
                                                         @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemRequestClient.getAllOwnerRequestItem(from, size, authorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestItem(@RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemRequestClient.getAllRequestItem(from, size, authorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestItem(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return itemRequestClient.getRequestItem(requestId, authorId);
    }
}
