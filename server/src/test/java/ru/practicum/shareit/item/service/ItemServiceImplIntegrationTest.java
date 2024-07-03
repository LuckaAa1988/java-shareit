package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    private User user;
    private Item item;


    @BeforeAll
    void setUp() {
        user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        item = Item.builder()
                .name("Drill")
                .description("Cordless drill")
                .user(user)
                .isAvailable(true)
                .build();

        userRepository.saveAndFlush(user);
        itemRepository.saveAndFlush(item);
    }

    @Test
    void testGetItem() throws NotFoundException {
        ItemResponse itemResponse = itemService.getItem(user.getId(), item.getId());

        assertNotNull(itemResponse);
        assertEquals(item.getId(), itemResponse.getId());
        assertEquals(item.getName(), itemResponse.getName());
        assertEquals(item.getDescription(), itemResponse.getDescription());
        assertEquals(item.getIsAvailable(), itemResponse.getIsAvailable());
        assertEquals(user.getId(), itemResponse.getUserId());
        assertNull(itemResponse.getLastBooking());
        assertNull(itemResponse.getNextBooking());
        assertTrue(itemResponse.getComments().isEmpty());
    }

    @Test
    void testGetItemNotFound() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            itemService.getItem(1L, 999L);
        });

        assertEquals("ITEM с id 999 не существует", thrown.getMessage());
    }
}
