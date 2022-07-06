package ru.yandex.practicum.filmorate.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;

import javax.xml.bind.ValidationException;
import java.util.Map;

@RestControllerAdvice(basePackages = {"ru.yandex.practicum.filmorate.controllers",
    "ru.yandex.practicum.filmorate.storage",
    "ru.yandex.practicum.filmorate.service"})
public class ErrorHandler {

    @ExceptionHandler({FilmValidationException.class, UserValidationException.class
            , MarkValidationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final Exception e) {
        return Map.of("error: ", "validation failed");
    }

    @ExceptionHandler({FilmNotFoundException.class
            , UserNotFoundException.class
            , MpaNotFoundException.class
            , GenreNotFoundException.class
            , DirectorNotFoundException.class
            , ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final Exception e) {
        return Map.of("error: ", "object not found");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        return Map.of(e.getClass().toString(), e.getMessage());
    }
}
