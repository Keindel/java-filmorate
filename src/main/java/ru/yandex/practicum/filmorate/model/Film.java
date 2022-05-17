package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @DurationMin(seconds = 1)
    private Duration duration;
}
