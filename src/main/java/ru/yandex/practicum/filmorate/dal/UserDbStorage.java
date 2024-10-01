package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseDbStorage<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users as u WHERE u.id IN (select friends_id from friendship where user_id = ?)";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friendship(user_id, friends_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friends_id = ?";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public Optional<User> findByEmail(long userId) {
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
        List<User> friendsUser = findFriends(userId);
        List<User> friendsOtherUser = findFriends(otherId);

        List<User> mutualFriends = new ArrayList<>();
        for (User friend : friendsUser) {
            if (friendsOtherUser.contains(friend)) {
                mutualFriends.add(friend);
            }
        }

        return mutualFriends;
    }
}
