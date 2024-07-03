package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "items", source = "items")
    ItemRequestResponse toDto(ItemRequest itemRequest, List<ItemResponse> items);

    @Mapping(target = "author", source = "author")
    ItemRequest fromDto(ItemRequestRequest itemRequestRequest, User author);

}
