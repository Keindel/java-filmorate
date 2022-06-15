package ru.yandex.practicum.filmorate.storage.impl;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private static int nextId = 1;

    @Override
    public long getSize() {
        return users.size();
    }

    @Override
    public User getById(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) throw new UserNotFoundException();
        return users.get(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        System.out.println("debug1");
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        System.out.println("debug2");
        return user;
    }

    @SneakyThrows
    @Override
    public void update(User user) {
        if (user.getId() <= 0 || !users.containsKey(user.getId())) throw new UserNotFoundException();
        users.put(user.getId(), user);
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
