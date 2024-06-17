package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.Status;
import ru.practicum.shareit.exception.model.AccessDeniedException;
import ru.practicum.shareit.exception.model.ItemException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User booker;
    private Item item;
    private Comment comment;
    private CommentRequest commentRequest;
    private Booking booking;
    private ItemCreate itemCreate1;
    private ItemCreate itemCreate2;
    private ItemCreate itemCreateWithRequest;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booker = User.builder().id(2L).name("Booker").email("booker@example.com").build();
        user = User.builder().id(1L).name("Author").email("author@example.com").build();
        item = Item.builder().id(2L).description("Powerful drill").name("Drill").isAvailable(true).user(user).build();
        comment = Comment.builder().id(1L).item(item).author(booker).text("new comment").build();
        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .id(1L)
                .status(Status.APPROVED)
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1)).build();
        commentRequest = new CommentRequest();
        commentRequest.setText("new comment");
        commentRequest.setAuthorId(1L);

        itemCreate1 = new ItemCreate();
        itemCreate1.setDescription("Powerful drill");
        itemCreate1.setName("Drill");
        itemCreate1.setRequestId(1L);
        itemCreate1.setIsAvailable(true);

        itemCreate2 = new ItemCreate();
        itemCreate2.setDescription("Big ladder");
        itemCreate2.setName("Ladder");
        itemCreate2.setIsAvailable(true);

        itemCreateWithRequest = new ItemCreate();
        itemCreateWithRequest.setDescription("Big ladder");
        itemCreateWithRequest.setName("Ladder");
        itemCreateWithRequest.setIsAvailable(true);
        itemCreateWithRequest.setRequestId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
    }


    @Test
    void testCreateItem() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });

        ItemResponse response = itemService.createItem(itemCreate2, user.getId());

        assertNotNull(response);
        assertEquals("Big ladder", response.getDescription());
        assertEquals("Ladder", response.getName());
        assertNull(response.getRequestId());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testCreateItemWithRequestId() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemResponse response = itemService.createItem(itemCreate1, user.getId());

        assertNotNull(response);
        assertEquals("Powerful drill", response.getDescription());
        assertEquals("Drill", response.getName());
        assertEquals(1L, response.getRequestId());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testUserNotFoundCreateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.createItem(itemCreate1, 1L));

        assertEquals("USER с id 1 не существует", exception.getMessage());
    }

    @Test
    void testUpdateItem() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.updateItem(itemCreate2, user.getId(), item.getId());

        assertNotNull(response);
        assertEquals("Big ladder", response.getDescription());
        assertEquals("Ladder", response.getName());
        assertTrue(response.getIsAvailable());
    }

    @Test
    void testItemNotFoundUpdateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemCreate1, 1L, 1L));

        assertEquals("ITEM с id 1 не существует", exception.getMessage());
    }

    @Test
    void testAccessDeniedUpdateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                itemService.updateItem(itemCreate1, 3L, 1L));

        assertEquals("USER с id 3 не может редактировать этот ITEM c id 1", exception.getMessage());
    }

    @Test
    void testGetItem() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemResponse response = itemService.getItem(2L,2L);

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
    void testUserNotFoundGetAllItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAllItems(1L, 0, 10));
    }

    @Test
    void testGetItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    void testSearchItems() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.searchItems("new comment")).thenReturn(List.of(item));

        List<ItemResponse> responses = itemService.searchItems("new comment", 1L, 0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(item.getId(), responses.get(0).getId());
        assertEquals(item.getName(), responses.get(0).getName());
        assertEquals(item.getDescription(), responses.get(0).getDescription());
    }

    @Test
    void testUserNotFoundSearchItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.searchItems("new comment", 1L, 0, 10));
    }

    @Test
    void testTextBlankSearchItems() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<ItemResponse> response = itemService.searchItems("", 1L, 0, 10);

        assertEquals(Collections.emptyList(), response);
    }

    @Test
    void testAddComment() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(List.of(booking));

        CommentResponse response = itemService.addComment(commentRequest, booker.getId(), item.getId());

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(commentRequest.getText(), response.getText());
        assertEquals(booker.getName(), response.getAuthorName());
    }

    @Test
    void testUserNotFoundAddComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(commentRequest, 1L, 2L));
    }

    @Test
    void testUserDidntBookItemAddComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        assertThrows(ItemException.class, () -> itemService.addComment(commentRequest, 1L, 2L));
    }
}