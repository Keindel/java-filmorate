package ru.yandex.practicum.filmorate.model;

import jdk.jfr.Timespan;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private final Set<Long> usersIdsLiked = new HashSet<>();

    public void addLike(Long userId) {
        usersIdsLiked.add(userId);
    }

    public void removeLike(Long userId) {
        usersIdsLiked.remove(userId);
    }
}
