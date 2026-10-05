package com.microservice.archchatmessagingservice.application.exceptions;

public class MessageNotFoundException extends RuntimeException{
    public MessageNotFoundException (String message){
        super(message);
    }
}
