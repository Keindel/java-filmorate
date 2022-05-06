package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    private static int nextId = 1;

    @GetMapping()
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping()
    public User create(@Valid @RequestBody @NonNull User user) throws UserValidationException {
//        validateUser(user);
        if (users.containsValue(user)) throw new UserValidationException();
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        log.info("user created with id = {}, number of users = {}", user.getId(), users.size());
        return user;
    }

    private void validateUser(@NonNull User user) throws UserValidationException {
//        if (user == null
//                || user.getEmail().isBlank()
//                || !user.getEmail().contains("@")
//                || user.getLogin().isBlank()
//                || user.getLogin().contains("\\s")
//                || user.getBirthday().isAfter(LocalDate.now()) )
        {
            log.warn("user validation fail");
            throw new UserValidationException();
        }
    }

    @PutMapping
    public void update(@Valid @RequestBody @NonNull User user) throws UserValidationException {
//        validateUser(user);
        users.put(user.getId(), user);
        log.info("user updated or created with id = " + user.getId());
    }
}
