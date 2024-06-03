package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.util.Constants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.repository.BookingSpecification.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponse createItem(ItemCreate itemCreate, Long userId) throws NotFoundException {
        log.info("Получен запрос на создание новой Item с именем {} от USER с id: {}", itemCreate.getName(), userId);
        if (notExist(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        var user = userRepository.findById(userId).get();
        var item = itemRepository.saveAndFlush(ItemMapper.INSTANCE.fromDto(itemCreate, user));
        return ItemMapper.INSTANCE.toDto(item);
    }

    @Override
    public ItemResponse updateItem(ItemCreate itemCreate,
                                   Long userId,
                                   Long itemId) throws NotFoundException, AccessDeniedException {
        log.info("Получен запрос на обновление Item с id {} от USER с id: {}", itemId, userId);
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId)));
        if (!item.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    String.format("USER с id %s не может редактировать этот ITEM c id %s", userId, itemId));
        }
        if (itemCreate.getName() != null) {
            item.setName(itemCreate.getName());
        }
        if (itemCreate.getDescription() != null) {
            item.setDescription(itemCreate.getDescription());
        }
        if (itemCreate.getIsAvailable() != null) {
            item.setIsAvailable(itemCreate.getIsAvailable());
        }
        itemRepository.saveAndFlush(item);
        return ItemMapper.INSTANCE.toDto(item);
    }

    @Override
    public ItemResponse getItem(Long userId, Long itemId) throws NotFoundException {
        log.info("Получен запрос на получение Item с id {} от USER с id: {}", itemId, userId);
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(Constants.ITEM_NOT_FOUND, itemId)));
        ItemBookingResponse nextBookingResponse = null;
        ItemBookingResponse lastBookingResponse = null;
        if (item.getUser().getId().equals(userId)) {
            nextBookingResponse = getNextBooking(itemId);
            lastBookingResponse = getLastBooking(itemId);
        }
        var comments = commentRepository.findAllByItemId(itemId);
        var commentsResponse = comments.stream()
                .map(CommentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        return ItemMapper.INSTANCE.toDtoWithBooking(item, nextBookingResponse, lastBookingResponse, commentsResponse);
    }

    @Override
    public List<ItemResponse> getAllItems(Long userId) throws NotFoundException {
        log.info("Получен запрос на получение списка всех Item от USER с id: {}", userId);
        if (notExist(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        var comments = commentRepository.findAll();
        var bookingsAfter = bookingRepository.findAll(startDateIsAfter().and(orderByAsc()));
        var bookingsBefore = bookingRepository.findAll(startDateIsBefore());
        return itemRepository.findAllByUserId(userId).stream()
                .map(i -> {
                    ItemBookingResponse nextBookingResponse = null;
                    if (!bookingsAfter.isEmpty()) {
                        nextBookingResponse = bookingsAfter.stream().filter(b ->
                                        b.getStatus().equals(Status.APPROVED)
                                                && b.getItem().getId().equals(i.getId()))
                                .map(BookingMapper.INSTANCE::toDtoItemBooking)
                                .findFirst().orElse(null);
                    }
                    ItemBookingResponse lastBookingResponse = null;
                    if (!bookingsBefore.isEmpty()) {
                        lastBookingResponse = bookingsBefore.stream().filter(b ->
                                        b.getStatus().equals(Status.APPROVED)
                                                && b.getItem().getId().equals(i.getId()))
                                .map(BookingMapper.INSTANCE::toDtoItemBooking)
                                .findFirst().orElse(null);
                    }
                    var commentsResponse = comments.stream()
                            .filter(c -> c.getItem().getId().equals(i.getId()))
                            .map(CommentMapper.INSTANCE::toDto)
                            .collect(Collectors.toList());
                    return ItemMapper.INSTANCE.toDtoWithBooking(
                            i, nextBookingResponse, lastBookingResponse, commentsResponse);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> searchItems(String text, Long userId) throws NotFoundException {
        log.info("Получен запрос на получение списка всех доступных Item " +
                        "содержащих текст \"{}\" в названии от USER с id: {}", text, userId);
        if (notExist(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text.toLowerCase()).stream()
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse addComment(CommentRequest commentRequest, Long userId, Long itemId)
            throws NotFoundException, ItemException {
        log.info("Получен запрос на создание нового Comment к Item с id {} от USER с id: {}", itemId, userId);
        if (notExist(userId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId));
        }
        var bookings = bookingRepository.findAll(byBookerId(userId)
                .and(byItemId(itemId))
                .and(byStatus(Status.APPROVED))
                .and(startDateIsBefore()));
        if (bookings.isEmpty()) {
            throw new ItemException(String.format("USER с id %s не брал ITEM c id %s ", userId, itemId));
        }
        var author = userRepository.findById(userId).get();
        var item = itemRepository.findById(itemId).get();
        var comment = commentRepository.save(CommentMapper.INSTANCE.fromDto(commentRequest, author, item));
        return CommentMapper.INSTANCE.toDto(comment);
    }

    private boolean notExist(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }

    private ItemBookingResponse getNextBooking(Long itemId) {
        ItemBookingResponse nextBookingResponse = null;
        var nextBooking = bookingRepository.findAll(byItemId(itemId)
                        .and(byStatus(Status.APPROVED))
                        .and(startDateIsAfter())
                        .and(orderByAsc()),
                PageRequest.of(0, 1));
        if (!nextBooking.isEmpty()) {
            nextBookingResponse = BookingMapper.INSTANCE.toDtoItemBooking(nextBooking.stream().findFirst().get());
        }
        return nextBookingResponse;
    }

    private ItemBookingResponse getLastBooking(Long itemId) {
        ItemBookingResponse lastBookingResponse = null;
        var lastBooking = bookingRepository.findAll(byItemId(itemId)
                        .and(byStatus(Status.APPROVED))
                        .and(startDateIsBefore()),
                PageRequest.of(0, 1));
        if (!lastBooking.isEmpty()) {
            lastBookingResponse = BookingMapper.INSTANCE.toDtoItemBooking(lastBooking.stream().findFirst().get());
        }
        return lastBookingResponse;
    }
}
