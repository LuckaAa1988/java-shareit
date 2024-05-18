package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public ItemDto createItem(Item item) {
        item.setId(id);
        items.put(id++,item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Item finById(Long itemId) throws NotFoundException {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else throw new NotFoundException("Id " + itemId + " не найден");
    }

    @Override
    public ItemDto updateItem(Item item, Long itemId) throws NotFoundException {
        if (items.containsKey(itemId)) {
            Item updatedItem = items.get(itemId);
            if (item.getName() != null) {
                items.get(itemId).setName(item.getName());
            }
            if (item.getDescription() != null) {
                items.get(itemId).setDescription(item.getDescription());
            }
            if (item.getIsAvailable() != null) {
                items.get(itemId).setIsAvailable(item.getIsAvailable());
            }
            return ItemMapper.toItemDto(updatedItem);
        } else throw new NotFoundException("Id " + itemId + " не найден");
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> i.getUserId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return items.values().stream()
                .filter(i -> (StringUtils.containsIgnoreCase(i.getName(), text)
                              || StringUtils.containsIgnoreCase(i.getDescription(), text))
                              && i.getIsAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
