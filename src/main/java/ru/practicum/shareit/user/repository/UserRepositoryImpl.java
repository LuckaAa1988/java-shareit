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
    private List<String> emails = new ArrayList<>();
    private Long id = 1L;

    @Override
    public UserDto createUser(User user) throws EmailException {
        if (emails.contains(user.getEmail())) {
            throw new EmailException("Этот email " + user.getEmail() + " уже используется");
        }
        emails.add(user.getEmail());
        user.setId(id);
        users.put(id++, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Optional<UserDto> finById(Long userId) {
        if (users.containsKey(userId)) {
            return Optional.ofNullable(UserMapper.toUserDto(users.get(userId)));
        }
        return Optional.empty();
    }

    /* Не нашёл решения проще,
    как соотвествовать всем критериям обновления юзера без использования БД,
    только такая громоздкая конструкция получилась */
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws UpdateException, EmailException {
        if (users.containsKey(userId)) {
            User updatedUser = users.get(userId);
            if (userDto.getEmail() != null && userDto.getName() != null &&
                (userDto.getEmail().equals(users.get(userId).getEmail()) || !emails.contains(userDto.getEmail()))) {
                users.get(userId).setName(userDto.getName());
                emails.remove(users.get(userId).getEmail());
                users.get(userId).setEmail(userDto.getEmail());
                emails.add(userDto.getEmail());
            } else if (userDto.getEmail() == null) {
                users.get(userId).setName(userDto.getName());
            } else if (userDto.getName() == null &&
                (userDto.getEmail().equals(users.get(userId).getEmail()) || !emails.contains(userDto.getEmail()))) {
                emails.remove(users.get(userId).getEmail());
                users.get(userId).setEmail(userDto.getEmail());
                emails.add(userDto.getEmail());
            } else throw new EmailException("Этот email " + userDto.getEmail() + " уже используется");
            return UserMapper.toUserDto(updatedUser);
        } else throw new UpdateException("Ошибка обвноления пользователя с id " + userId);
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            emails.remove(users.get(userId).getEmail());
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
}
