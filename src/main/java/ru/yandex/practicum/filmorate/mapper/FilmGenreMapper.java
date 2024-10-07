package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreMapper implements RowMapper<FilmGenreDto> {
    @Override
    public FilmGenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenreDto genre = new FilmGenreDto();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        genre.setFilmId(rs.getLong("film_id"));
        return genre;
    }
}
