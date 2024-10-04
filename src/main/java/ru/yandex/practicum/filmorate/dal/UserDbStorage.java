package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class UserDbStorage extends BaseDbStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users as u WHERE u.id IN (select friends_id from friendship where user_id = ?)";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friendship(user_id, friends_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friends_id = ?";
    private static final String FIND_MUTUAL_FRIEND = "select * from users u, friendship f1, friendship f2 where u.id = f1.FRIENDS_ID AND u.id = f2.FRIENDS_ID AND f1.USER_ID = ? AND f2.USER_ID = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public User findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User findByEmail(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public void addFriends(long userId, long friendId) {
        long id = insert(
                ADD_FRIEND,
                userId,
                friendId
        );
    }

    public void deleteFriend(long userId, long friendId) {
        deleteParams(DELETE_FRIEND,  userId, friendId);
    }

    public List<User> findFriends(long userId) {
        return findMany(FIND_FRIENDS_QUERY, userId);
    }

    public List<User> findMutualFriends(long userId, long otherId) {
        return findMany(FIND_MUTUAL_FRIEND, userId, otherId);
    }
}
