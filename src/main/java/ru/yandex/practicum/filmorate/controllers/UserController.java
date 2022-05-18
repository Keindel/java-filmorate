package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Storage<User> userStorage;
    private final UserService userService;
    @Autowired
    public UserController(Storage<User> userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping()
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping()
    public User create(@Valid @RequestBody @NonNull User user) {
        user = userStorage.create(user);
        log.info("user created with id = {}, number of users = {}", user.getId(), userStorage.getSize());
        return user;
    }

    @PutMapping
    public void update(@Valid @RequestBody @NonNull User user) {
        userStorage.update(user);
        log.info("user with id = {} updated or created", user.getId());
    }
}
