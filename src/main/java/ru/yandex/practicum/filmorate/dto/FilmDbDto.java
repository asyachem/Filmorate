package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmDbDto {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    private Long genreId;
    private String genreName;

    private Long mpaId;
    private String mpaName;
}
