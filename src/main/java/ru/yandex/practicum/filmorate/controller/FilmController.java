package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage filmStorage;

    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        return filmStorage.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Long filmId,
                             @PathVariable("userId") Long userId) {
        return filmStorage.deleteLike(filmId, userId);
    }
}
