package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        userRepository.saveAndFlush(author);
    }

    @Test
    void testFindAllByAuthor() {
        ItemRequest request1 = ItemRequest.builder()
                .description("First request")
                .author(author)
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Second request")
                .author(author)
                .build();

        itemRequestRepository.saveAndFlush(request1);
        itemRequestRepository.saveAndFlush(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByAuthorId(author.getId(), PageRequest.of(0, 10));
        assertEquals(2, requests.size());
        assertTrue(requests.contains(request1));
        assertTrue(requests.contains(request2));
    }

    @Test
    void testFindAll() {
        User anotherAuthor = User.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();
        userRepository.saveAndFlush(anotherAuthor);

        ItemRequest request1 = ItemRequest.builder()
                .description("First request")
                .author(author)
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Second request")
                .author(anotherAuthor)
                .build();

        itemRequestRepository.saveAndFlush(request1);
        itemRequestRepository.saveAndFlush(request2);

        List<ItemRequest> requests = itemRequestRepository.findAll();
        assertEquals(2, requests.size());
        assertTrue(requests.contains(request1));
        assertTrue(requests.contains(request2));
    }

    @Test
    void testFindAllWithPagination() {
        ItemRequest request1 = ItemRequest.builder()
                .description("Request 1")
                .author(author)
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Request 2")
                .author(author)
                .build();

        itemRequestRepository.saveAndFlush(request1);
        itemRequestRepository.saveAndFlush(request2);

        List<ItemRequest> firstPage = itemRequestRepository.findAll(PageRequest.of(0, 1)).getContent();
        List<ItemRequest> secondPage = itemRequestRepository.findAll(PageRequest.of(1, 1)).getContent();

        assertEquals(1, firstPage.size());
        assertEquals(1, secondPage.size());
        assertTrue(firstPage.contains(request1) || firstPage.contains(request2));
        assertTrue(secondPage.contains(request1) || secondPage.contains(request2));
    }

    @Test
    void testFindAllByAuthorWithPagination() {
        ItemRequest request1 = ItemRequest.builder()
                .description("Request 1")
                .author(author)
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Request 2")
                .author(author)
                .build();

        itemRequestRepository.saveAndFlush(request1);
        itemRequestRepository.saveAndFlush(request2);

        List<ItemRequest> firstPage = itemRequestRepository.findAllByAuthorId(author.getId(), PageRequest.of(0, 1));
        List<ItemRequest> secondPage = itemRequestRepository.findAllByAuthorId(author.getId(), PageRequest.of(1, 1));

        assertEquals(1, firstPage.size());
        assertEquals(1, secondPage.size());
        assertTrue(firstPage.contains(request1) || firstPage.contains(request2));
        assertTrue(secondPage.contains(request1) || secondPage.contains(request2));
    }
}