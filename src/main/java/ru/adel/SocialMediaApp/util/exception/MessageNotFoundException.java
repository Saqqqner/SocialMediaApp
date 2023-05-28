package ru.adel.SocialMediaApp.util.exception;

public class MessageNotFoundException extends RuntimeException{
    public MessageNotFoundException(String msg){
        super(msg);
    }
}
