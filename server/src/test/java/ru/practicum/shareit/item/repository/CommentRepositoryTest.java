package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private User userItemRequest;
    private ItemRequest itemRequest;
    private Item item;

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
        item = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .isAvailable(true)
                .user(user)
                .build();
        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(userItemRequest);
        itemRepository.saveAndFlush(item);

        itemRequest = ItemRequest.builder()
                .description("Need a drill")
                .author(userItemRequest)
                .build();

        itemRequestRepository.saveAndFlush(itemRequest);
    }

    @Test
    void testFindAllByItemId() {
        Comment comment = Comment.builder()
                .text("new comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentRepository.saveAndFlush(comment);

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertEquals(1, comments.size());
        assertTrue(comments.contains(comment));
    }

}