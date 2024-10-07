package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findById(long genreId) {
        return genreDbStorage.findById(genreId);
    }

    public HashMap<Long, List<Genre>> findAllByFilmId(List<Long> ids) {
        return genreDbStorage.findAllByFilmId(ids);
    }
}
