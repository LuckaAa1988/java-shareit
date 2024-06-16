package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemResponse mockItemResponse;
    private ItemCreate mockItemCreate;

    @BeforeEach
    void setUp() {
        mockItemResponse = new ItemResponse();
        mockItemResponse.setId(1L);
        mockItemResponse.setName("Item1");
        mockItemResponse.setDescription("Description1");
        mockItemResponse.setIsAvailable(true);
        mockItemResponse.setUserId(1L);

        mockItemCreate = new ItemCreate();
        mockItemCreate.setName("Item1");
        mockItemCreate.setDescription("Description1");
        mockItemCreate.setIsAvailable(true);
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(any(ItemCreate.class), anyLong())).thenReturn(mockItemResponse);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockItemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockItemResponse.getId()))
                .andExpect(jsonPath("$.name").value(mockItemResponse.getName()))
                .andExpect(jsonPath("$.description").value(mockItemResponse.getDescription()));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(any(ItemCreate.class), anyLong(), anyLong())).thenReturn(mockItemResponse);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockItemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockItemResponse.getId()))
                .andExpect(jsonPath("$.name").value(mockItemResponse.getName()))
                .andExpect(jsonPath("$.description").value(mockItemResponse.getDescription()));
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(mockItemResponse);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockItemResponse.getId()))
                .andExpect(jsonPath("$.name").value(mockItemResponse.getName()))
                .andExpect(jsonPath("$.description").value(mockItemResponse.getDescription()));
    }

    @Test
    void testGetAllItems() throws Exception {
        List<ItemResponse> mockItems = Arrays.asList(mockItemResponse);
        when(itemService.getAllItems(anyLong(), any(), any())).thenReturn(mockItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockItemResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(mockItemResponse.getName()))
                .andExpect(jsonPath("$[0].description").value(mockItemResponse.getDescription()));
    }

    @Test
    void testSearchItems() throws Exception {
        List<ItemResponse> mockItems = Arrays.asList(mockItemResponse);
        when(itemService.searchItems(anyString(), anyLong(), any(), any())).thenReturn(mockItems);

        mockMvc.perform(get("/items/search")
                        .param("text", "searchText")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockItemResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(mockItemResponse.getName()))
                .andExpect(jsonPath("$[0].description").value(mockItemResponse.getDescription()));
    }

    @Test
    void testAddCommentItem() throws Exception {
        CommentRequest mockCommentRequest = new CommentRequest();
        mockCommentRequest.setText("Comment");
        CommentResponse mockCommentResponse = new CommentResponse();
        mockCommentResponse.setId(1L);
        mockCommentResponse.setText("Comment");
        mockCommentResponse.setAuthorName("Author");

        when(itemService.addComment(any(CommentRequest.class), anyLong(), anyLong())).thenReturn(mockCommentResponse);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCommentResponse.getId()))
                .andExpect(jsonPath("$.text").value(mockCommentResponse.getText()))
                .andExpect(jsonPath("$.authorName").value(mockCommentResponse.getAuthorName()));
    }

    @Test
    void testCreateItemNotFoundException() throws Exception {
        when(itemService.createItem(any(ItemCreate.class), anyLong())).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockItemCreate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateItemAccessDeniedException() throws Exception {
        when(itemService.updateItem(any(ItemCreate.class), anyLong(), anyLong())).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockItemCreate)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetItemNotFoundException() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddCommentItemException() throws Exception {
        CommentRequest mockCommentRequest = new CommentRequest();
        mockCommentRequest.setText("Comment");

        when(itemService.addComment(any(CommentRequest.class), anyLong(), anyLong())).thenThrow(new ItemException("Comment error"));

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(mockCommentRequest)))
                .andExpect(status().isBadRequest());
    }
}