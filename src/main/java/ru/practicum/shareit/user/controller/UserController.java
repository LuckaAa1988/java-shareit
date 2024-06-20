package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) throws NotFoundException {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserRequest userRequest) throws NotFoundException {
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId,
                                   @RequestBody UserRequest userRequest) throws NotFoundException {
        return userService.updateUser(userId, userRequest);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUserById(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return userService.findAll(from, size);
    }
}
