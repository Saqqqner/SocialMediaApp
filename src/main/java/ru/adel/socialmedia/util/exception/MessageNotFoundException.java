package ru.adel.socialmedia.util.exception;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(String msg) {
        super(msg);
    }
}
