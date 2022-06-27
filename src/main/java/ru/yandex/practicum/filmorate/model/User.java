package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private long id;
    @NonNull
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S+")
    private final String login;
    @NonNull
    private String name;
    @NonNull
    @PastOrPresent
    private LocalDate birthday;

    private Map<Long, FriendshipStatus> friends;

    public String getName(){
        if (name.isBlank()) return login;
        return name;
    }
}
