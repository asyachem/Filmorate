package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;

    public UserService(UserStorage userStorage, UserDbStorage userDbStorage) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll()  {
        return userDbStorage.findAll();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("ошибка при вводе почты");
            throw new ValidationException("Почта не может быть пустым полем и должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().indexOf(' ') != -1) {
            log.warn("ошибка при вводе логина");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            if (user.getLogin() == null || user.getLogin().indexOf(' ') != -1) {
                log.warn("ошибка при вводе логина");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("ошибка при вводе даты рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getId() != null) {
            if (userDbStorage.findByEmail(user.getId()).isPresent()) {
                log.warn("уже существует пользователь с заданным имейлом");
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

        return userDbStorage.save(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User updatedUser = userDbStorage.findById(newUser.getId())
                .map(user -> UserMapper.updateUserFields(user, newUser))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updatedUser = userDbStorage.update(updatedUser);

        return updatedUser;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг пользователя не найден"));

        userDbStorage.addFriends(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг пользователя не найден"));

        List<User> friends = userDbStorage.findFriends(userId);
        User friend = userDbStorage.findById(friendId).get();
        if (!friends.contains(friend)) {
            log.warn("Пользователи не друзья, удаление из друзей отклонено");
        }

        userDbStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> findUserFriends(Long userId) {
        if (userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        userDbStorage.findById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

       return userDbStorage.findFriends(userId);
    }

    public Collection<User> findMutualFriends(Long userId, Long otherId) {
        if (userId == null || otherId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        return userDbStorage.findMutualFriends(userId, otherId);
    }
}
