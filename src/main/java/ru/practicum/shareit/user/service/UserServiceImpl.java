package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.util.Constants;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUser(Long userId) throws NotFoundException {
        var user = userRepository.findById(userId);
        return UserMapper.INSTANCE.toDto(user.orElseThrow(
                () -> new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId))));
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
            var user = userRepository.saveAndFlush(UserMapper.INSTANCE.fromDto(userRequest));
            return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) throws NotFoundException {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId)));
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        userRepository.saveAndFlush(user);
        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        userRepository.deleteById(userId);
        return !userRepository.existsById(userId);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
}
