package com.microservice.archchatmessagingservice.application.exceptions;

public class ChatNotFoundException extends RuntimeException{

    public ChatNotFoundException(String message){
        super(message);
    }
}
