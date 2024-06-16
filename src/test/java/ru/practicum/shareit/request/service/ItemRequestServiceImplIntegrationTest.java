package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User author;
    private User anotherUser;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeAll
    void setUp() {
        author = createUser("Author", "author@example.com");
        anotherUser = createUser("Another User", "anotheruser@example.com");
        itemRequest = createItemRequest("Need a ladder", author);
        item = createItem("Ladder", "A tall ladder", anotherUser, itemRequest);
        userRepository.saveAndFlush(author);
        userRepository.saveAndFlush(anotherUser);
        itemRequestRepository.saveAndFlush(itemRequest);
        itemRepository.saveAndFlush(item);
    }

    @Test
    void testGetAllRequestItem() throws NotFoundException, StateException {
        List<ItemRequestResponse> itemRequests = itemRequestService.getAllRequestItem(0, 10, anotherUser.getId());

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
        assertFalse(itemRequests.get(0).getItems().isEmpty());
        assertEquals(item.getId(), itemRequests.get(0).getItems().get(0).getId());
    }

    @Test
    void testGetAllRequestItemNotFound() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllRequestItem(0, 10, 999L);
        });

        assertEquals("USER с id 999 не существует", thrown.getMessage());
    }

    @Test
    void testGetAllRequestItemStateException() {
        StateException thrown = assertThrows(StateException.class, () -> {
            itemRequestService.getAllRequestItem(0, 0, anotherUser.getId());
        });

        assertEquals("размер не может быть 0", thrown.getMessage());
    }

    @Test
    void testGetAllRequestItemExcludingAuthor() throws NotFoundException, StateException {
        List<ItemRequestResponse> itemRequests = itemRequestService.getAllRequestItem(0, 10, author.getId());

        assertNotNull(itemRequests);
        assertTrue(itemRequests.isEmpty());
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private ItemRequest createItemRequest(String description, User author) {
        return ItemRequest.builder()
                .description(description)
                .author(author)
                .build();
    }

    private Item createItem(String name, String description, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .name(name)
                .description(description)
                .user(owner)
                .itemRequest(itemRequest)
                .isAvailable(true)
                .build();
    }
}
