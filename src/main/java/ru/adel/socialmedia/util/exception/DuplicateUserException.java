package ru.adel.socialmedia.util.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}
