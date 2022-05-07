package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private static int nextId = 1;

    @GetMapping()
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping()
    public User create(@Valid @RequestBody @NonNull User user) {
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        log.info("user created with id = {}, number of users = {}", user.getId(), users.size());
        return user;
    }

    @PutMapping
    public void update(@Valid @RequestBody @NonNull User user) {
        users.put(user.getId(), user);
        log.info("user updated or created with id = " + user.getId());
    }
}
