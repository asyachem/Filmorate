package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage userStorage;

    public UserController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long userId,
                          @PathVariable("friendId") Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Long userId,
                          @PathVariable("friendId") Long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<Long> findUserFriends(@PathVariable("id") Long userId) {
        return userStorage.findUserFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<Long> findMutualFriends(@PathVariable("id") Long userId,
                                              @PathVariable("otherId") Long otherId) {
        return userStorage.findMutualFriends(userId, otherId);
    }
}
