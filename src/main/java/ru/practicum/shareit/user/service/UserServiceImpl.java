package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.model.EmailException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserDto> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) throws EmailException {
        return userRepository.createUser(UserMapper.toUser(userDto));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws UpdateException, EmailException {
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll();
    }
}
