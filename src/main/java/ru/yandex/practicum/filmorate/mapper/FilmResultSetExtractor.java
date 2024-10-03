package ru.yandex.practicum.filmorate.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
        List<Film> films = new ArrayList<>();
        Film film = null;
        while (rs.next()) {
            Long id = rs.getLong("id");
            if (film == null || !id.equals(film.getId())) {
                if(film != null) {
                    films.add(film);
                }
                film = Film.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .genres(new ArrayList<>())
                        .build();
            }
            long mpaId = rs.getLong("mpa_id");
            if (mpaId != 0) {
                Mpa mpa = new Mpa();
                mpa.setId(mpaId);
                mpa.setName(rs.getString("mpa_name"));
                film.setMpa(mpa);
            }
            long genreId = rs.getLong("genre_id");
            if (genreId != 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }
        if (film != null) {
            films.add(film);
        }
        return films;
}
}