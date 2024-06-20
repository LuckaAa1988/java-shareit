package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private User userItemRequest;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        userItemRequest = User.builder()
                .name("John Doe Request")
                .email("john.doe.request@example.com")
                .build();
        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(userItemRequest);

        itemRequest = ItemRequest.builder()
                .description("Need a drill")
                .author(userItemRequest)
                .build();

        itemRequestRepository.saveAndFlush(itemRequest);
    }

    @Test
    void testFindAllByUserId() {
        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .isAvailable(true)
                .user(user)
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .isAvailable(true)
                .user(user)
                .build();

        itemRepository.saveAndFlush(item1);
        itemRepository.saveAndFlush(item2);

        List<Item> items = itemRepository.findAllByUserId(user.getId(), PageRequest.of(0, 10));
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    @DirtiesContext
    void testSearchItems() {
        Item item1 = Item.builder()
                .name("Drill")
                .description("Electric drill for home use")
                .isAvailable(true)
                .user(user)
                .build();

        Item item2 = Item.builder()
                .name("Hammer")
                .description("Heavy-duty hammer")
                .isAvailable(true)
                .user(user)
                .build();

        Item item3 = Item.builder()
                .name("Saw")
                .description("Hand saw")
                .isAvailable(false)
                .user(user)
                .build();

        itemRepository.saveAndFlush(item1);
        itemRepository.saveAndFlush(item2);
        itemRepository.saveAndFlush(item3);

        List<Item> results = itemRepository.searchItems("drill");
        assertEquals(1, results.size());
        assertTrue(results.contains(item1));

        results = itemRepository.searchItems("hammer");
        assertEquals(1, results.size());
        assertTrue(results.contains(item2));

        results = itemRepository.searchItems("saw");
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindAllByItemRequest() {
        Item item1 = Item.builder()
                .name("Drill")
                .description("Electric drill for home use")
                .isAvailable(true)
                .user(user)
                .itemRequest(itemRequest)
                .build();

        Item item2 = Item.builder()
                .name("Screwdriver")
                .description("Philips screwdriver")
                .isAvailable(true)
                .user(user)
                .itemRequest(itemRequest)
                .build();

        itemRepository.saveAndFlush(item1);
        itemRepository.saveAndFlush(item2);

        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    void testFindAllByUserIdWithPagination() {
        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .isAvailable(true)
                .user(user)
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .isAvailable(true)
                .user(user)
                .build();

        itemRepository.saveAndFlush(item1);
        itemRepository.saveAndFlush(item2);

        List<Item> firstPage = itemRepository.findAllByUserId(user.getId(), PageRequest.of(0, 1));
        List<Item> secondPage = itemRepository.findAllByUserId(user.getId(), PageRequest.of(1, 1));

        assertEquals(1, firstPage.size());
        assertEquals(1, secondPage.size());
        assertTrue(firstPage.contains(item1) || firstPage.contains(item2));
        assertTrue(secondPage.contains(item1) || secondPage.contains(item2));
    }
}