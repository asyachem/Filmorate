package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User create(User user);
    User update(User newUser);
}
