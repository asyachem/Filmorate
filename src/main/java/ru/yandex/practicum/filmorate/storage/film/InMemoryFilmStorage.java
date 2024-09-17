package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Set<Long> usersLiked = new HashSet<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    final static Integer MAX_LENGTH_DESCRIPTION = 200;
    final static LocalDate OLD_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;

    public InMemoryFilmStorage(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("ошибка при вводе названия фильма");
            throw new ConditionsNotMetException("Имя фильма не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            log.warn("ошибка при вводе описания фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(OLD_RELEASE_DATE)) {
            log.warn("ошибка при вводе даты релиза фильма");
            throw new ValidationException("Дата релиза фильма — не раньше 28 декабря 1895 года;");
        }
        if (film.getDuration() < 0) {
            log.warn("ошибка при вводе продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                if (newFilm.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                    log.warn("ошибка при вводе описания фильма");
                    throw new ValidationException("Максимальная длина описания — 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(OLD_RELEASE_DATE)) {
                    log.warn("ошибка при вводе даты релиза фильма");
                    throw new ValidationException("Дата релиза фильма — не раньше 28 декабря 1895 года;");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != 0) {
                if (newFilm.getDuration() < 0) {
                    log.warn("ошибка при вводе продолжительности фильма");
                    throw new ValidationException("Продолжительность фильма должна быть положительным числом");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
            return oldFilm;
        }
        log.warn("фильм с заданным айди не был обнаружен");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Film likeFilm(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (usersLiked.contains(userId)) {
            log.warn("этот пользователь уже ставил лайк");
            throw new ValidationException("Этот пользователь уже ставил лайк");
        }
        if (films.containsKey(filmId)) {
            usersLiked.add(userId);
            return filmService.likeFilm(films.get(filmId), userId);
        }

        log.warn("фильм с заданным айди не был обнаружен");
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (films.containsKey(filmId)) {
            usersLiked.remove(userId);
            return filmService.deleteLike(films.get(filmId), userId);
        }

        log.warn("фильм с заданным айди не был обнаружен");
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }
}
