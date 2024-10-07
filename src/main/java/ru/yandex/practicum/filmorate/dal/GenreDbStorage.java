package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT g.id, g.name, fg.film_id FROM genres g" +
            " inner join film_genres fg on g.id=fg.genre_id " +
            " where fg.film_id in (%s) order by g.id";

    private FilmGenreMapper filmGenreMapper;

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper, FilmGenreMapper filmGenreMapper) {
        super(jdbc, mapper);
        this.filmGenreMapper = filmGenreMapper;
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public HashMap<Long, List<Genre>> findAllByFilmId(List<Long> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String query = FIND_BY_FILM_ID_QUERY.replace("%s", inSql);
        List<FilmGenreDto> dtos = jdbc.query(query, filmGenreMapper, filmIds.toArray());
        HashMap<Long, List<Genre>> filmGenres = new HashMap<>();
        for(FilmGenreDto dto : dtos) {
            List<Genre> genres = filmGenres.get(dto.getFilmId());
            if (genres == null) {
                genres = new ArrayList<>();
                filmGenres.put(dto.getFilmId(), genres);
            }
            genres.add(new Genre(dto.getId(), dto.getName()));
        }
        return filmGenres;
    }
}
