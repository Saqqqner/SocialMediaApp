package ru.adel.SocialMediaApp.util.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String msg){
        super(msg);
    }
}
