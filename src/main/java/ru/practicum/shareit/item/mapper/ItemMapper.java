package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemBookingResponse;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemResponse toDto(Item item);

    @Mapping(target = "id", expression = "java(item.getId())")
    @Mapping(target = "userId", expression = "java(item.getUser().getId())")
    ItemResponse toDtoWithBooking(
            Item item, ItemBookingResponse nextBooking,
            ItemBookingResponse lastBooking, List<CommentResponse> comments);

    @Mapping(target = "name", expression = "java(itemCreate.getName())")
    @Mapping(target = "user", source = "user")
    Item fromDto(ItemCreate itemCreate, User user);
}
