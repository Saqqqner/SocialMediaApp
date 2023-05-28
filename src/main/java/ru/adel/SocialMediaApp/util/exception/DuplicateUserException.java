package ru.adel.SocialMediaApp.util.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}
