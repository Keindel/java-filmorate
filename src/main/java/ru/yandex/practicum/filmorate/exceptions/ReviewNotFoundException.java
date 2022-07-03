package ru.yandex.practicum.filmorate.exceptions;


public class ReviewNotFoundException extends Exception{

    public ReviewNotFoundException() {
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
