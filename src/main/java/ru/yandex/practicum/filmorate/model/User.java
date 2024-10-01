package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    // todo
    @JsonIgnore
    private Set<Long> friends;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;

    public boolean hasEmail() {
       return email != null && !email.isEmpty();
    }

    public boolean hasLogin() {
        return login != null && !login.isEmpty();
    }

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
