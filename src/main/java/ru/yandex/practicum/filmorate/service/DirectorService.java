package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.DirectorDbStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {


    private final DirectorDbStorage directorStorage;

    public Director create(Director director) throws DirectorValidationException {
       return directorStorage.create(director);
    }

    public Director getDirectorById(long id) throws UserNotFoundException, FilmNotFoundException, DirectorNotFoundException {
        return directorStorage.getById(id);
    }

    public Collection<Director> getDirectors() {
        return directorStorage.findAll();
    }

    public long getSize() {
        return directorStorage.getSize();
    }

    public void update(Director director) throws DirectorNotFoundException {
        directorStorage.update(director);

    }

    public void deleteDirectorById(long id) throws DirectorNotFoundException {
       directorStorage.deleteById(id);
    }
}
