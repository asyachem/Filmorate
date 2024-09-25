package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void put(Film film);
    Film get(long id);
    Collection<Film> getAll();
    long getNextId();
    boolean containsFilm(long id);
}
