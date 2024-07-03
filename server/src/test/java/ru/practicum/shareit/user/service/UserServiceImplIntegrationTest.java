package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUpdateUser() throws NotFoundException {
        User user = createUser("Jane Doe", "jane.doe@example.com");
        userRepository.saveAndFlush(user);
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setName("Jane Smith");
        updateUserRequest.setEmail("jane.smith@example.com");

        UserResponse updatedUser = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("Jane Smith", updatedUser.getName());
        assertEquals("jane.smith@example.com", updatedUser.getEmail());

        User retrievedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Jane Smith", retrievedUser.getName());
        assertEquals("jane.smith@example.com", retrievedUser.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setName("Nonexistent User");

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, updateUserRequest);
        });

        assertEquals("USER с id 999 не существует", thrown.getMessage());
    }

    @Test
    void testUpdateUserNoEmail() throws NotFoundException {
        User user = createUser("John Doe", "john.doe.no.email@example.com");
        userRepository.saveAndFlush(user);
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setName("John Updated");

        UserResponse updatedUser = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("John Updated", updatedUser.getName());
        assertEquals("john.doe.no.email@example.com", updatedUser.getEmail());

        User retrievedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("John Updated", retrievedUser.getName());
        assertEquals("john.doe.no.email@example.com", retrievedUser.getEmail());
    }

    @Test
    void testUpdateUserNoName() throws NotFoundException {
        User user = createUser("Bob Doe", "bob.doe@example.com");
        userRepository.saveAndFlush(user);
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setEmail("bob.updated@example.com");

        UserResponse updatedUser = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("Bob Doe", updatedUser.getName());
        assertEquals("bob.updated@example.com", updatedUser.getEmail());

        User retrievedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Bob Doe", retrievedUser.getName());
        assertEquals("bob.updated@example.com", retrievedUser.getEmail());
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }
}