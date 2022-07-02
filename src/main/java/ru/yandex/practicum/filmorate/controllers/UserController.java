package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @GetMapping()
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return userService.getById(id).getFriends().keySet()
                .stream()
                .map(id1 -> {
                    try {
                        return userService.getById(id1);
                    } catch (UserNotFoundException | FilmNotFoundException | MpaNotFoundException |
                             GenreNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends (@PathVariable Long id, @PathVariable Long otherId) throws UserNotFoundException, FilmNotFoundException, MpaNotFoundException, GenreNotFoundException {
        return userService.getMutualFriendsIds(id, otherId).stream()
                .map(id1 -> {
                    try {
                        return userService.getById(id1);
                    } catch (UserNotFoundException | FilmNotFoundException | MpaNotFoundException |
                             GenreNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @PostMapping()
    public User create(@Valid @RequestBody @NonNull User user) {
        user = userService.create(user);
        log.info("user created with id = {}, number of users = {}", user.getId(), userService.getSize());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody @NonNull User user) throws UserNotFoundException, FilmNotFoundException {
        userService.update(user);
        log.info("user with id = {} updated or created", user.getId());
        return user;
    }

    /**
     * добавление в друзья
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void requestFriendship(@PathVariable Long id, @PathVariable Long friendId) throws UserNotFoundException, FilmNotFoundException {
        boolean result = feedService.updateFriend(id, friendId);
        userService.requestFriendship(id, friendId);
        if (! result) {
            feedService.addFriend(id, friendId);
        }
    }

    /**
     * удаление из друзей
     * @param id
     * @param friendId
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendship(@PathVariable Long id, @PathVariable Long friendId) throws UserNotFoundException, FilmNotFoundException {
        feedService.deleteFriend(id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) throws UserNotFoundException, FilmNotFoundException {
        userService.deleteById(userId);
    }
}
