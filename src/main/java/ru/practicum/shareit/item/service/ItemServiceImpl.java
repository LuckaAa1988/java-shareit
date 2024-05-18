package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException {
        if (userRepository.finById(userId).isPresent()) {
            return itemRepository.createItem(ItemMapper.toItem(itemDto, userId));
        } else throw new NotFoundException("Юзера с id " + userId + " не существует");
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto,
                              Long userId,
                              Long itemId) throws NotFoundException, AccessDeniedException {
        if (!itemRepository.finById(itemId).getUserId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        return itemRepository.updateItem(ItemMapper.toItem(itemDto, userId), itemId);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) throws NotFoundException {
        return ItemMapper.toItemDto(itemRepository.finById(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) throws NotFoundException {
        if (userRepository.finById(userId).isPresent()) {
            return itemRepository.findAllByUserId(userId);
        } else throw new NotFoundException("Юзера с id " + userId + " не существует");
    }

    @Override
    public List<ItemDto> searchItems(String text, Long userId) throws NotFoundException {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (userRepository.finById(userId).isPresent()) {
            return itemRepository.searchItems(text.toLowerCase());
        } else throw new NotFoundException("Юзера с id " + userId + " не существует");
    }
}
