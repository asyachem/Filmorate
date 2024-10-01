package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.LongMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_GENRE_QUERY = "SELECT genre_id FROM film_genres where film_id=?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_USER_LIKES_QUERY = "SELECT * FROM users WHERE id IN (select users_id from film_users where film_id = ?)";

    //private static final String FIND_BY_ID_QUERY = "select *, g.genre_id from films as f INNER JOIN film_genres as g ON f.id = g.film_id where f.id = ?";

    private static final String FIND_POPULAR_QUERY = "select * from films AS f inner join (select film_id, count(*) as cnt from film_users group by film_id ) AS uf on uf.film_id = f.id order by uf.cnt DESC LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, mpa, release_date, duration) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_users (film_id, users_id) " +
            "VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, mpa = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_LIKED_USER_QUERY = "DELETE FROM film_users WHERE film_id = ? AND users_id = ?";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private RowMapper<User> userRowMapper;
    private RowMapper<Long> longRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<User> userRowMapper, RowMapper<Long> longRowMapper) {
        super(jdbc, mapper);
        this.userRowMapper = userRowMapper;
        this.longRowMapper = longRowMapper;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(long filmId) {
        // не получается получить список всех айди жанров
       //   List<Long> genres = JdbcTemplate.query(FIND_GENRE_QUERY, longRowMapper, filmId);

        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public Film save(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                insert(INSERT_GENRE_QUERY, film.getId(), genre.getId());
            });
        }

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );

        // ОБНОВЛЕНИЕ ЖАНРОВ В FILM_GENRES
        deleteParams(DELETE_GENRES_QUERY,  film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                insert(INSERT_GENRE_QUERY, film.getId(), genre.getId());
            });
        }

        return film;
    }

    public void addlike(long filmId, long userId) {
        List<User> users = jdbc.query(FIND_USER_LIKES_QUERY, userRowMapper, filmId);

        if (!users.isEmpty()) {
            for (User user : users) {
                if (user.getId().equals(userId)) {
                    log.warn("Этот пользователь уже ставил лайк");
                    throw new ValidationException("Лайк уже поставлен!");
                }
            }
        }

        long id = insert(
                INSERT_LIKE_QUERY,
                filmId,
                userId
        );
    }

    public void deleteLikedUser(long filmId, long userId) {
        if (!deleteParams(DELETE_LIKED_USER_QUERY, filmId, userId)) {
            throw new InternalServerException("Не удалось удалить данные");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }
}
