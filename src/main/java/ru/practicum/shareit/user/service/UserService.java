package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUser(Long userId) throws NotFoundException;

    UserResponse createUser(UserRequest userRequest) throws NotFoundException;

    UserResponse updateUser(Long userId, UserRequest userRequest) throws NotFoundException;

    boolean deleteUser(Long userId);

    List<UserResponse> findAll(Integer from, Integer size);
}
