package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class FilmGenreDto extends FilmDto {
    private Long id;
    private String name;
    private Long filmId;
}
