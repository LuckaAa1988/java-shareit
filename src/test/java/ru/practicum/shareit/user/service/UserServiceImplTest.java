package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserRequest mockUserRequest;
    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");

        mockUserRequest = new UserRequest();
        mockUserRequest.setName("John Doe");
        mockUserRequest.setEmail("john.doe@example.com");

        mockUserResponse = UserMapper.INSTANCE.toDto(mockUser);
    }

    @Test
    void testGetUser() throws NotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        UserResponse userResponse = userService.getUser(1L);

        assertNotNull(userResponse);
        assertEquals(mockUser.getId(), userResponse.getId());
        assertEquals(mockUser.getName(), userResponse.getName());
        assertEquals(mockUser.getEmail(), userResponse.getEmail());
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void testCreateUser() {
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(mockUser);

        UserResponse userResponse = userService.createUser(mockUserRequest);

        assertNotNull(userResponse);
        assertEquals(mockUser.getId(), userResponse.getId());
        assertEquals(mockUser.getName(), userResponse.getName());
        assertEquals(mockUser.getEmail(), userResponse.getEmail());
    }

    @Test
    void testUpdateUser() throws NotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(mockUser);

        UserRequest updatedRequest = new UserRequest();
        updatedRequest.setName("Jane Doe");
        updatedRequest.setEmail("jane.doe@example.com");

        UserResponse userResponse = userService.updateUser(1L, updatedRequest);

        assertNotNull(userResponse);
        assertEquals("Jane Doe", userResponse.getName());
        assertEquals("jane.doe@example.com", userResponse.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserRequest updatedRequest = new UserRequest();
        updatedRequest.setName("Jane Doe");

        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, updatedRequest));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.deleteUserById(anyLong())).thenReturn(1);

        boolean isDeleted = userService.deleteUser(1L);

        assertTrue(isDeleted);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.deleteUserById(anyLong())).thenReturn(0);

        boolean isDeleted = userService.deleteUser(1L);

        assertFalse(isDeleted);
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll(anyInt(), anyInt())).thenReturn(List.of(mockUser));

        List<UserResponse> users = userService.findAll(0, 10);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(mockUser.getId(), users.get(0).getId());
        assertEquals(mockUser.getName(), users.get(0).getName());
        assertEquals(mockUser.getEmail(), users.get(0).getEmail());
    }
}