package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
//import lombok.NonNull;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private int id;
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
}
