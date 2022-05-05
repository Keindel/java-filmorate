package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Set<User> users = new HashSet<>();

    @GetMapping()
    public Set<User> findAll() {
        return users;
    }

    @PostMapping()
    public void create(@RequestBody User user) throws UserValidationException {
        if (users.contains(user)) throw new UserValidationException();
        validateUser(user);
        users.add(user);
        log.info("user created, total number = " + users.size());
    }

    private void validateUser(User user) throws UserValidationException {
        if (user == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                || user.getLogin().isBlank()
                || user.getLogin().contains("\\s")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("user validation fail");
            throw new UserValidationException();
        }
    }

    @PutMapping
    public void update(@RequestBody User user) throws UserValidationException {
        validateUser(user);
        users.remove(user);
        users.add(user);
        log.info("user updated or created with id = " + user.getId());
    }
}
