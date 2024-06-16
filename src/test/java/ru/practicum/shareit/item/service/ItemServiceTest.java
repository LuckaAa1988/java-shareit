package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Booking booking;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).name("Author").email("author@example.com").build();
        item = Item.builder().id(2L).description("Powerful drill").name("Drill").isAvailable(true).user(user).build();
        booking = Booking.builder().build();
    }


    @Test
    void testCreateItem() throws Exception {
        ItemCreate itemCreate = new ItemCreate();
        itemCreate.setDescription("Powerful drill");
        itemCreate.setName("Drill");
        itemCreate.setIsAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        ItemResponse response = itemService.createItem(itemCreate, user.getId());

        assertNotNull(response);
        assertEquals("Powerful drill", response.getDescription());
        assertEquals("Drill", response.getName());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemCreate itemCreate = new ItemCreate();
        itemCreate.setDescription("Big ladder");
        itemCreate.setName("Ladder");
        itemCreate.setIsAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.updateItem(itemCreate, user.getId(), item.getId());

        assertNotNull(response);
        assertEquals("Big ladder", response.getDescription());
        assertEquals("Ladder", response.getName());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testGetItem() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemResponse response = itemService.getItem(2L,2L);

        assertNotNull(response);
        assertNotNull(response);
        assertEquals("Powerful drill", response.getDescription());
        assertEquals("Drill", response.getName());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testGetAllItems() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item));

        List<ItemResponse> responses = itemService.getAllItems(1L,0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(item.getId(), responses.get(0).getId());
        assertEquals(item.getName(), responses.get(0).getName());
        assertEquals(item.getDescription(), responses.get(0).getDescription());
    }

    @Test
    void testGetItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }
}