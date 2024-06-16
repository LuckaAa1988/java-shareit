package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("DELETE FROM User WHERE id = :userId")
    int deleteUserById(Long userId);

    @Query(value = "SELECT * FROM users LIMIT :size OFFSET :from", nativeQuery = true)
    List<User> findAll(Integer from, Integer size);
}