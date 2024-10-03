package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> {
    private static final String FIND_FILMS_QUERY = "select f.id, f.name, f.description, f.release_date, f.duration, g.id as genre_id, g.name as genre_name, m.id as mpa_id, m.name as mpa_name from films f left join film_genres fg on f.id = fg.film_id left join genres g on g.id = fg.genre_id left join mpas m on m.id = f.mpa";
    private static final String FIND_ALL_QUERY = FIND_FILMS_QUERY + " order by f.id, g.id";
    private static final String FIND_BY_ID_QUERY = FIND_FILMS_QUERY + " where f.id=? order by f.id, g.id";
    private static final String FIND_USER_LIKES_QUERY = "SELECT * FROM users WHERE id IN (select users_id from film_users where film_id = ?)";
    private static final String FIND_POPULAR_QUERY = "select * from films AS f inner join (select film_id, count(*) as cnt from film_users group by film_id) AS uf on uf.film_id = f.id order by uf.cnt DESC LIMIT ?";
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

    private FilmResultSetExtractor filmResultSetExtractor;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, RowMapper<User> userRowMapper, FilmResultSetExtractor filmResultSetExtractor) {
        super(jdbc, mapper);
        this.userRowMapper = userRowMapper;
        this.filmResultSetExtractor = filmResultSetExtractor;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY, filmResultSetExtractor);
    }

    public Optional<Film> findById(long filmId) {
        List<Film> films = findMany(FIND_BY_ID_QUERY, filmResultSetExtractor, filmId);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.getFirst());
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

        updateFilmGenre(film);

        return film;
    }

    private void updateFilmGenre(Film film) {
        if (film.getGenres() != null) {

            List<Object[]> batch = new ArrayList<>();
            for (Long genreId : film.getGenres().stream().map(Genre::getId).distinct().toList()) {
                Object[] values = new Object[] {
                        film.getId(), genreId};
                batch.add(values);
            }

            jdbc.batchUpdate(INSERT_GENRE_QUERY, batch);
        }
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
        updateFilmGenre(film);

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
