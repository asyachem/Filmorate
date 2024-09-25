package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
    public boolean containsUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public User putUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) {
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
