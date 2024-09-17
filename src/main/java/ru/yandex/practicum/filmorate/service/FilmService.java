package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmService {
    public Film likeFilm(Film film, Long userId) {
        film.getLikedUsersId().add(userId);
        return film;
    }

    public Film deleteLike(Film film, Long userId) {
        film.getLikedUsersId().remove(userId);
        return film;
    }
}
