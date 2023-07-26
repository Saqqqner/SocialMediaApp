package ru.adel.socialmedia.util.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String msg) {
        super(msg);
    }
}
