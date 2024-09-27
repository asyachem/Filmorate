package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final HashSet<String> emails = new HashSet<>();

    @Override
    public Collection<User> getAll()  {
        return users.values();
    }

    @Override
    public boolean containsEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public User putUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) {
        if (users.get(id) == null) {
            log.warn("пользователь с заданным айди не был обнаружен");
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public void putEmail(String email) {
        emails.add(email);
    }

    @Override
    public void removeUser(long id) {
        users.remove(id);
    }

    @Override
    public void removeEmail(String id) {
        emails.remove(id);
    }

    @Override
    public void addEmail(String id) {
        emails.add(id);
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
