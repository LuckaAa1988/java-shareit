package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.exception.model.UpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.model.EmailException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    UserDto createUser(User user) throws EmailException;

    Optional<UserDto> finById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto) throws UpdateException, EmailException;

    boolean deleteUser(Long userId);

    List<UserDto> findAll();
}
