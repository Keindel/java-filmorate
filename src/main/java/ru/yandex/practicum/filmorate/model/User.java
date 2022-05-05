package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private final int id;
    @NonNull
    private String email;
    @NonNull
    private final String login;
    @NonNull
    private String name;
    @NonNull
    private LocalDate birthday;
}
