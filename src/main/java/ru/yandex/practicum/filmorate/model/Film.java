package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

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
    private Set<Genre> genres;
    private Mpa mpa;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    private int duration;

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }
    public boolean hasMpa() {
        return mpa != null;
    }
    public boolean hasReleaseDate() {
        return releaseDate != null;
    }
    public boolean hasDuration() {
        return duration > 0;
    }
}
