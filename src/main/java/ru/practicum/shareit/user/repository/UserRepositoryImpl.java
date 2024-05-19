package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.UpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.model.EmailException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public UserDto createUser(User user) throws EmailException {
        if (checkEmail(user.getEmail())) {
            throw new EmailException("Этот email " + user.getEmail() + " уже используется");
        }
        user.setId(id);
        users.put(id++, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Optional<UserDto> findById(Long userId) {
        if (users.containsKey(userId)) {
            return Optional.ofNullable(UserMapper.toUserDto(users.get(userId)));
        }
        return Optional.empty();
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws UpdateException, EmailException {
        if (findById(userId).isPresent()) {
            User updatedUser = users.get(userId);
            if (userDto.getEmail() != null
                && checkEmail(userDto.getEmail())
                && !userDto.getEmail().equals(updatedUser.getEmail())) {
                throw new EmailException("Этот email " + userDto.getEmail() + " уже используется");
            }
            if (userDto.getName() != null) {
                updatedUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                updatedUser.setEmail(userDto.getEmail());
            }
            return UserMapper.toUserDto(updatedUser);
        } else throw new UpdateException("Ошибка обвноления пользователя с id " + userId);
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (findById(userId).isPresent()) {
            users.remove(userId);
            return true;
        }
        return false;
    }

    @Override
    public List<UserDto> findAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean checkEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }
}
