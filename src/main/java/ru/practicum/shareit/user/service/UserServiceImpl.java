package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUser(Long userId) throws NotFoundException {
        log.info("Получение User по id: {}", userId);
        var user = userRepository.findById(userId);
        return userMapper.toDto(user.orElseThrow(
                () -> new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId))));
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Создание нового User с именем: {}", userRequest.getName());
        var user = userRepository.saveAndFlush(userMapper.fromDto(userRequest));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) throws NotFoundException {
        log.info("Обновление User с id: {}", userId);
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.USER_NOT_FOUND, userId)));
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        userRepository.saveAndFlush(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        log.info("Удаление User с id: {}", userId);
        return userRepository.deleteUserById(userId) > 0;
    }

    @Override
    public List<UserResponse> findAll(Integer from, Integer size) {
        log.info("Получение списка всех User");
        if (from < 0) throw new RuntimeException("from не может быть меньше 0");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
