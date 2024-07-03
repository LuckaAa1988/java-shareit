package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User author;
    private ItemRequest itemRequest;
    private ItemRequestRequest request;
    private Item item;
    private UserResponse userResponse;
    private ItemRequestResponse itemRequestResponse;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).name("Author").email("author@example.com").build();
        itemRequest = ItemRequest.builder().id(1L).description("Need a ladder").author(author).build();
        item = Item.builder().id(1L).name("Ladder").description("A tall ladder").user(author).itemRequest(itemRequest).build();
        request = new ItemRequestRequest();
        request.setDescription("Need a ladder");
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Author");
        userResponse.setEmail("author@example.com");
        itemRequestResponse = new ItemRequestResponse();
        itemRequestResponse.setId(1L);
        itemResponse = new ItemResponse();
        itemResponse.setId(1L);
        itemRequestResponse.setItems(List.of(itemResponse));
        itemRequestResponse.setDescription("Need a ladder");
    }

    @Test
    void testAddRequestItem() throws NotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest req = invocation.getArgument(0);
            req.setId(1L);
            return req;
        });
        when(itemRepository.findAllByItemRequest(any(ItemRequest.class))).thenReturn(List.of(item));
        when(itemRequestMapper.fromDto(any(ItemRequestRequest.class), any(User.class))).thenReturn(itemRequest);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemResponse);
        when(itemRequestMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestResponse);

        ItemRequestResponse response = itemRequestService.addRequestItem(request, 1L);

        assertNotNull(response);
        assertEquals("Need a ladder", response.getDescription());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void testAddRequestItemUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.addRequestItem(request, 1L);
        });

        assertEquals("USER с id 1 не существует", thrown.getMessage());
    }

    @Test
    void testGetAllRequestItemInvalidSize() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        ArithmeticException thrown = assertThrows(ArithmeticException.class, () -> {
            itemRequestService.getAllRequestItem(0, 0, 1L);
        });
    }

    @Test
    void testGetAllOwnerRequestItem() throws NotFoundException, StateException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRequestRepository.findAllByAuthorId(anyLong(), any(Pageable.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByItemRequestAuthorId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemResponse);
        when(itemRequestMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestResponse);

        List<ItemRequestResponse> responses = itemRequestService.getAllOwnerRequestItem(0, 10, 1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(itemRequest.getId(), responses.get(0).getId());
    }

    @Test
    void testGetRequestItem() throws NotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequest(itemRequest)).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemResponse);
        when(itemRequestMapper.toDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestResponse);

        ItemRequestResponse response = itemRequestService.getRequestItem(1L, 1L);

        assertNotNull(response);
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void testGetRequestItemUserNotFound() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestItem(1L, 1L);
        });

        assertEquals("USER с id 1 не существует", thrown.getMessage());
    }

    @Test
    void testGetRequestItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestItem(1L, 1L);
        });

        assertEquals("ITEM с id 1 не существует", thrown.getMessage());
    }
}
