package ru.yandex.practicum.filmorate.exception;

public class CreationFailException extends RuntimeException {
    public CreationFailException(String message) {
        super(message);
    }
}
