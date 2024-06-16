package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    List<UserResponse> userResponses;


    @BeforeEach
    void setUp() {
        User one = User.builder()
                .id(1L)
                .email("alice@example.com")
                .name("Alice")
                .build();
        User two = User.builder()
                .id(2L)
                .email("bob@example.com")
                .name("Bob")
                .build();
        userResponses = List.of(UserMapper.INSTANCE.toDto(one), UserMapper.INSTANCE.toDto(two));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUser(1L)).thenReturn(userResponses.get(0));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.findAll(0, 0)).thenReturn(userResponses);

        mockMvc.perform(get("/users?from=0&size=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"));


    }

    @Test
    void testCreateUserSuccess() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Alice");
        userRequest.setEmail("alice@example.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequest);
        when(userService.createUser(userRequest)).thenReturn(UserMapper.INSTANCE.toDto(User.builder()
                        .id(1L)
                        .email("alice@example.com")
                        .name("Alice")
                        .build()));


        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testCreateUserFailEmail() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Alice");
        userRequest.setEmail("NO EMAIL");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequest);
        when(userService.createUser(userRequest)).thenReturn(UserMapper.INSTANCE.toDto(User.builder()
                .id(1L)
                .email("alice@example.com")
                .name("Alice")
                .build()));


        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("John Doe");
        userRequest.setEmail("john.doe@example.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequest);
        when(userService.updateUser(1L, userRequest)).thenReturn(UserMapper.INSTANCE.toDto(User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .name("John Doe")
                .build()));


        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }
}