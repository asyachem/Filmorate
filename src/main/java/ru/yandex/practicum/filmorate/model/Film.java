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
    private Long id;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    private int duration;
}
