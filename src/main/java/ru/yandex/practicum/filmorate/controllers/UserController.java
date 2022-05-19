package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id){
        return userService.getById(id).getFriends()
                .stream()
                .map(userService::getById)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends (@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getMutualFriendsIds(id, otherId).stream()
                .map(userService::getById)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public User create(User user) {
        user = userService.create(user);
        log.info("user created with id = {}, number of users = {}", user.getId(), userService.getSize());
        return user;
    }

    @PutMapping
    public User update(User user) {
        userService.update(user);
        log.info("user with id = {} updated or created", user.getId());
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendship(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }
}
