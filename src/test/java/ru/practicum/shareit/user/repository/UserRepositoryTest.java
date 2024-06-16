package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void testSaveAndFindById() {
        userRepository.saveAndFlush(mockUser);

        Optional<User> foundUser = userRepository.findById(mockUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    @Transactional
    void testDeleteUserById() {
        userRepository.saveAndFlush(mockUser);

        int deletedCount = userRepository.deleteUserById(mockUser.getId());
        assertEquals(1, deletedCount);

        em.clear();

        Optional<User> deletedUser = userRepository.findById(mockUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testFindAllWithPagination() {
        User user1 = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .build();

        User user2 = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .build();

        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        List<User> users = userRepository.findAll(0, 1);
        assertEquals(1, users.size());

        List<User> allUsers = userRepository.findAll(0, 10);
        assertEquals(2, allUsers.size());
    }
}