package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    @Mapping(target = "id", expression = "java(comment.getId())")
    CommentResponse toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment fromDto(CommentRequest commentRequest, User author, Item item);
}
