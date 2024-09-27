package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    final static Integer MAX_LENGTH_DESCRIPTION = 200;
    final static LocalDate OLD_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

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
        filmStorage.put(film);
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film oldFilm = filmStorage.get(newFilm.getId());

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

    private long getNextId() {
        return filmStorage.getNextId();
    }

    public Film likeFilm(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film film = filmStorage.get(filmId);
        film.getLikedUsersId().add(userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film film = filmStorage.get(filmId);
        film.getLikedUsersId().remove(userId);
        return film;
    }
}
