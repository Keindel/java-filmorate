package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @EqualsAndHashCode.Include
    @Min(1)
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
    private final Set<Genre> genres = new HashSet<>();
    private AgeRating ageRating;

    public void addLike(Long userId) {
        usersIdsLiked.add(userId);
    }

    public void removeLike(Long userId) {
        usersIdsLiked.remove(userId);
    }
}
