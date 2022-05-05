package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Duration duration;
}
