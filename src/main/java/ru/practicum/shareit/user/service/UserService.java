package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.model.UpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.model.EmailException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> getUser(Long userId);

    UserDto createUser(UserDto userDto) throws EmailException;

    UserDto updateUser(Long userId, UserDto userDto) throws UpdateException, EmailException;

    boolean deleteUser(Long userId);

    List<UserDto> getAllUsers();
}
