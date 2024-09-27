package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.Genre;
import ru.yandex.practicum.filmorate.enums.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    @JsonIgnore
    private Set<Long> likedUsersId;

    private Genre[] genre;
    private Mpa rating;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    private int duration;
}
