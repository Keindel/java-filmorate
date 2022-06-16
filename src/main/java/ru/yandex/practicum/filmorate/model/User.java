package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    //TODO Map<Long, FriendshipStatus> friends = new HashMap<>()
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void deleteFriend(Long id) {
        friends.remove(id);
    }

    public String getName(){
        if (name.isBlank()) return login;
        return name;
    }
}
