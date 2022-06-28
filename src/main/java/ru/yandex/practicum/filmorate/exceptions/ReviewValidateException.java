package ru.yandex.practicum.filmorate.exceptions;


public class ReviewValidateException extends Exception{
    public ReviewValidateException() {
    }

    public ReviewValidateException(String message) {
        super(message);
    }
}
