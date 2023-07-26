package ru.adel.socialmedia.util.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String msg) {
        super(msg);
    }

}
