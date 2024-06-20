package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserRequest mockUserRequest;
    private UserResponse mockUserResponse;
    private UserResponse updateMockUserResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");

        mockUserRequest = new UserRequest();
        mockUserRequest.setName("John Doe");
        mockUserRequest.setEmail("john.doe@example.com");

        mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setName("John Doe");
        mockUserResponse.setEmail("john.doe@example.com");

        updateMockUserResponse = new UserResponse();
        updateMockUserResponse.setId(1L);
        updateMockUserResponse.setName("Jane Doe");
        updateMockUserResponse.setEmail("jane.doe@example.com");
    }

    @Test
    void testGetUser() throws NotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserResponse);

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
        when(userMapper.fromDto(mockUserRequest)).thenReturn(mockUser);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserResponse);

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
        when(userMapper.toDto(mockUser)).thenReturn(updateMockUserResponse);

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
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(mockUser)));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserResponse);

        List<UserResponse> users = userService.findAll(0, 10);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(mockUser.getId(), users.get(0).getId());
        assertEquals(mockUser.getName(), users.get(0).getName());
        assertEquals(mockUser.getEmail(), users.get(0).getEmail());
    }
}