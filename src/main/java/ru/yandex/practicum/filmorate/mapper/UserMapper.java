package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


@Component
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper implements RowMapper<User> {
    public static User updateUserFields(User user, User requestedUser) {
        if (requestedUser.hasName()) {
            user.setName(requestedUser.getName());
        }
        if (requestedUser.hasEmail()) {
            if (!requestedUser.getEmail().contains("@")) {
                log.warn("емейл указан без @");
                throw new ValidationException("Почта должна содержать @");
            }
            user.setEmail(requestedUser.getEmail());
        }
        if (requestedUser.hasLogin()) {
            if (requestedUser.getLogin().indexOf(' ') != -1) {
                log.warn("логин указан с пробелами");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            user.setLogin(requestedUser.getLogin());
        }
        if (requestedUser.hasBirthday()) {
            if (requestedUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("указали неверную дату рождения");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            user.setBirthday(requestedUser.getBirthday());
        }
        return user;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
