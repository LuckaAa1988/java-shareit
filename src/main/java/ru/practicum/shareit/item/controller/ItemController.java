package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse createItem(@RequestBody @Valid ItemCreate itemCreate,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.createItem(itemCreate, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestBody ItemCreate itemCreate,
                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long itemId) throws AccessDeniedException, NotFoundException {
        return itemService.updateItem(itemCreate, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId) throws NotFoundException {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItems(@RequestParam String text,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addCommentItem(@RequestBody @Valid CommentRequest commentRequest,
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) throws NotFoundException, ItemException {
        return itemService.addComment(commentRequest, userId, itemId);
    }

}
