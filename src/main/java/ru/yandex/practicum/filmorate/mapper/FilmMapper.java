package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper implements RowMapper<Film> {
    final static Integer MAX_LENGTH_DESCRIPTION = 200;
    final static LocalDate OLD_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public static Film updateFilmFields(Film film, Film requestFilm) {
        // todo как обновить жанры
        if (requestFilm.hasName()) {
            film.setName(requestFilm.getName());
        }
        if (requestFilm.hasDescription()) {
            if (requestFilm.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                log.warn("ошибка при вводе описания фильма");
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            film.setDescription(requestFilm.getDescription());
        }
        if (requestFilm.hasMpa()) {
            film.setMpa(requestFilm.getMpa());
        }
        if (requestFilm.hasReleaseDate()) {
            if (requestFilm.getReleaseDate().isBefore(OLD_RELEASE_DATE)) {
                log.warn("ошибка при вводе даты релиза фильма");
                throw new ValidationException("Дата релиза фильма — не раньше 28 декабря 1895 года;");
            }
            film.setReleaseDate(requestFilm.getReleaseDate());
        }
        if (requestFilm.hasDuration()) {
            film.setDuration(requestFilm.getDuration());
        } else {
            log.warn("ошибка при вводе продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (requestFilm.getGenres() != null) {
            film.setGenres(requestFilm.getGenres());
        }

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());


        if (film.getGenres() != null) {
            Set<Genre> genres = new HashSet<>();
            for (Genre genre : film.getGenres()) {
                genres.add(makeGenre(genre.getId()));
            }
            dto.setGenres(genres);
        } else {
            dto.setGenres(null);
        }

        if (film.getMpa() != null) {
            dto.setMpa(makeMpa(film.getMpa().getId()));
        } else {
            dto.setMpa(null);
        }

        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        return dto;
    }

    public static Genre makeGenre(long genreId) {
        Genre genre = new Genre();
        switch ((int) genreId) {
            case 1:
                genre.setId(genreId);
                genre.setName("Комедия");
                return genre;
            case 2:
                genre.setId(genreId);
                genre.setName("Драма");
                return genre;
            case 3:
                genre.setId(genreId);
                genre.setName("Мультфильм");
                return genre;
            case 4:
                genre.setId(genreId);
                genre.setName("Триллер");
                return genre;
            case 5:
                genre.setId(genreId);
                genre.setName("Документальный");
                return genre;
            case 6:
                genre.setId(genreId);
                genre.setName("Боевик");
                return genre;
            default:
                return null;
        }
    }

    public static Mpa makeMpa(long mpaId) {
        Mpa mpa = new Mpa();
        switch ((int) mpaId) {
            case 1:
                mpa.setId(mpaId);
                mpa.setName("G");
                return mpa;
            case 2:
                mpa.setId(mpaId);
                mpa.setName("PG");
                return mpa;
            case 3:
                mpa.setId(mpaId);
                mpa.setName("PG-13");
                return mpa;
            case 4:
                mpa.setId(mpaId);
                mpa.setName("R");
                return mpa;
            case 5:
                mpa.setId(mpaId);
                mpa.setName("NC-17");
                return mpa;
            default:
                return null;
        }
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Set<Genre> genres = new HashSet<>();
        genres.add(makeGenre(rs.getLong("genre_id")));

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(makeMpa(rs.getLong("mpa")))
                .genres(genres)
                .build();
    }
}
