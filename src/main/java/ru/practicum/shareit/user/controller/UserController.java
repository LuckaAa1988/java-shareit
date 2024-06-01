package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) throws NotFoundException {
        log.info("Получен GET запрос на получение User по id: {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserRequest userRequest) throws NotFoundException {
        log.info("Получен POST запрос на создание нового User с именем: {}", userRequest.getName());
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId,
                              @RequestBody UserRequest userRequest) throws NotFoundException {
        log.info("Получен PATCH запрос на обновление User с id: {}", userId);
        return userService.updateUser(userId, userRequest);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUserById(@PathVariable Long userId) {
        log.info("Получен DELETE запрос на удаление User с id: {}", userId);
        return userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.info("Получен GET запрос на получение списка всех User");
        return userService.findAll();
    }
}
