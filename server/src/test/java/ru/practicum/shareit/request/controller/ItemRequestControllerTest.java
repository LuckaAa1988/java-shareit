package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    List<ItemRequestResponse> mockResponses;

    @BeforeEach
    void setUp() {
        User u1 = User.builder().id(1L).name("Ivan").email("ivan@example.com").build();
        User u2 = User.builder().id(1L).name("Roman").email("roman@example.com").build();
        ItemRequest one = ItemRequest.builder()
                .id(1L)
                .author(u1)
                .description("i need spoon")
                .created(LocalDateTime.now())
                .build();
        ItemRequest two = ItemRequest.builder()
                .id(2L)
                .author(u2)
                .description("i need fork")
                .created(LocalDateTime.now())
                .build();
        Item itemOne = Item.builder()
                .id(1L)
                .user(u2)
                .name("Spoon")
                .description("Very good spoon")
                .isAvailable(true)
                .itemRequest(one)
                .build();
        Item itemTwo = Item.builder()
                .id(2L)
                .user(u1)
                .name("Fork")
                .description("Very good fork")
                .isAvailable(true)
                .itemRequest(two)
                .build();
        List<ItemResponse> itemsOne = List.of(ItemMapper.INSTANCE.toDto(itemOne));
        List<ItemResponse> itemsTwo = List.of(ItemMapper.INSTANCE.toDto(itemTwo));
        mockResponses = List.of(ItemRequestMapper.INSTANCE.toDto(one, itemsOne),
                ItemRequestMapper.INSTANCE.toDto(two, itemsTwo));
    }

    @Test
    void testGetItemRequest() throws Exception {
        when(itemRequestService.getRequestItem(1L, 2L)).thenReturn(mockResponses.get(0));

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("i need spoon"))
                .andExpect(jsonPath("$.created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Spoon"))
                .andExpect(jsonPath("$.items[0].description").value("Very good spoon"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(1));

    }

    @Test
    void testGetAllRequestItem() throws Exception {
        when(itemRequestService.getAllRequestItem(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(mockResponses);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("i need spoon"))
                .andExpect(jsonPath("$[0].created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Spoon"))
                .andExpect(jsonPath("$[0].items[0].description").value("Very good spoon"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].requestId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("i need fork"))
                .andExpect(jsonPath("$[1].created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$[1].items").isArray())
                .andExpect(jsonPath("$[1].items[0].id").value(2))
                .andExpect(jsonPath("$[1].items[0].name").value("Fork"))
                .andExpect(jsonPath("$[1].items[0].description").value("Very good fork"))
                .andExpect(jsonPath("$[1].items[0].available").value(true))
                .andExpect(jsonPath("$[1].items[0].requestId").value(2));
    }

    @Test
    void testGetAllRequestItemNotFoundException() throws Exception {
        when(itemRequestService.getAllRequestItem(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllRequestItemStateException() throws Exception {
        when(itemRequestService.getAllRequestItem(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenThrow(new StateException("Invalid State"));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllOwnerRequestItem() throws Exception {
        when(itemRequestService.getAllOwnerRequestItem(0, 10, 1L))
                .thenReturn(mockResponses);

        mockMvc.perform(get("/requests")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("i need spoon"))
                .andExpect(jsonPath("$[0].created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Spoon"))
                .andExpect(jsonPath("$[0].items[0].description").value("Very good spoon"))
                .andExpect(jsonPath("$[0].items[0].available").value(true))
                .andExpect(jsonPath("$[0].items[0].requestId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("i need fork"))
                .andExpect(jsonPath("$[1].created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$[1].items").isArray())
                .andExpect(jsonPath("$[1].items[0].id").value(2))
                .andExpect(jsonPath("$[1].items[0].name").value("Fork"))
                .andExpect(jsonPath("$[1].items[0].description").value("Very good fork"))
                .andExpect(jsonPath("$[1].items[0].available").value(true))
                .andExpect(jsonPath("$[1].items[0].requestId").value(2));
    }

    @Test
    void testGetAllOwnerRequestItemNotFoundException() throws Exception {
        when(itemRequestService.getAllOwnerRequestItem(0, 10, 1L))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/requests")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOwnerRequestItemStateException() throws Exception {
        when(itemRequestService.getAllOwnerRequestItem(0, 10, 1L))
                .thenThrow(new StateException("Invalid State"));

        mockMvc.perform(get("/requests")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSuccessItemRequest() throws Exception {
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest();
        itemRequestRequest.setDescription("i need spoon");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(itemRequestRequest);
        when(itemRequestService.addRequestItem(itemRequestRequest, 1L)).thenReturn(mockResponses.get(0));

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("i need spoon"))
                .andExpect(jsonPath("$.created").value(lessThan(LocalDateTime.now().toString())))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Spoon"))
                .andExpect(jsonPath("$.items[0].description").value("Very good spoon"))
                .andExpect(jsonPath("$.items[0].available").value(true))
                .andExpect(jsonPath("$.items[0].requestId").value(1));
    }

    @Test
    void testCreateNullDescriptionItemRequest() throws Exception {
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(itemRequestRequest);
        when(itemRequestService.addRequestItem(itemRequestRequest, 1L)).thenReturn(mockResponses.get(0));

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }
}