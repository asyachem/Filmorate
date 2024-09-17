package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film create(Film film);
    Film update (Film newFilm);
}
