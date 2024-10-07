package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    final static Integer MAX_LENGTH_DESCRIPTION = 200;
    final static LocalDate OLD_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmDbStorage filmDbStorage;
    private final GenreService genreService;

    public FilmService(FilmDbStorage filmDbStorage, GenreService genreService) {
        this.filmDbStorage = filmDbStorage;
        this.genreService = genreService;
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmDbStorage.findAll();

        HashMap<Long, List<Genre>> genres = genreService.findAllByFilmId(films.stream().map(Film::getId).toList());
        for (Film film : films) {
            film.setGenres(genres.get(film.getId()));
        }

        return films;
    }

    public Film findById(Long id) {
        if (id == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film result = filmDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        HashMap<Long, List<Genre>> genres = genreService.findAllByFilmId(List.of(id));
        if (!genres.isEmpty()) {
            result.setGenres(genres.get(id));
        }
        return result;
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
        if (film.getMpa() == null) {
            log.warn("ошибка при вводе рейтинга фильма - поле пустое");
            throw new ValidationException("Рейтинг фильма не может быть пустым");
        }
        if (checkMpa(film.getMpa().getId()) == null) {
            log.warn("ошибка при вводе рейтинга фильма - такого рейтинга нет");
            throw new ValidationException("Рейтинга фильма с таким айди нет");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (checkGenre(genre.getId()) == null) {
                    log.warn("ошибка при вводе жанра фильма - такого жанра нет");
                    throw new ValidationException("Жанра фильма с таким рейтингом нет");
                }
            }
        }

        filmDbStorage.save(film);
        return findById(film.getId());
    }

    public Mpa checkMpa(long mpaId) {
        return FilmMapper.makeMpa(mpaId);
    }

    public Genre checkGenre(long genreId) {
        return FilmMapper.makeGenre(genreId);
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film updatedFilm = filmDbStorage.findById(newFilm.getId())
                .map(film -> FilmMapper.updateFilmFields(film, newFilm))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updatedFilm = filmDbStorage.update(updatedFilm);

        return updatedFilm;
    }

    public void likeFilm(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Film film = filmDbStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        filmDbStorage.addlike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ошибка - не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        filmDbStorage.deleteLikedUser(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDbStorage.getPopularFilms(count);
    }
}
