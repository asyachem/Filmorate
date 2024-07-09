package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class Film {
    Long id;
    String name;
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    LocalDate releaseDate;
    int duration;
}
