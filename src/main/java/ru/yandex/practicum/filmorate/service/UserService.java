package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll()  {
        return userStorage.getAll();
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

        checkEmail(user);
        user.setId(getNextId());
        userStorage.putUser(user);
        userStorage.putEmail(user.getEmail());
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = userStorage.getUser(newUser.getId());

        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            checkEmail(newUser);
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            if (!newUser.getEmail().contains("@")) {
                log.warn("емейл указан без @");
                throw new ValidationException("Почта должна содержать @");
            }
            userStorage.removeEmail(oldUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
            userStorage.addEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            if (newUser.getLogin().indexOf(' ') != -1) {
                log.warn("логин указан с пробелами");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("указали неверную дату рождения");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            oldUser.setBirthday(newUser.getBirthday());
        }

        return oldUser;

    }

    private void checkEmail(User user) {
        if (userStorage.containsEmail(user.getEmail())) {
            log.warn("уже существует пользователь с заданным имейлом");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private long getNextId() {
        return userStorage.getNextId();
    }

    public User addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return userStorage.getUser(userId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    public Collection<Long> findUserFriends(Long userId) {
        if (userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        return new ArrayList<>(userStorage.getUser(userId).getFriends());
    }

    public Collection<Long> findMutualFriends(Long userId, Long otherId) {
        if (userId == null || otherId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherId);

        Set<Long> usersFriend = new HashSet<>(user.getFriends());
        usersFriend.retainAll(otherUser.getFriends());
        return usersFriend;
    }
}
