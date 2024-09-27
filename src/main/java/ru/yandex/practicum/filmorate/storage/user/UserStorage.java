package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();
    boolean containsEmail(String email);
    long getNextId();
    User putUser(User user);
    User getUser(long id);
    void putEmail(String email);
    void removeUser(long id);
    void removeEmail(String id);
    void addEmail(String id);
}
