package com.microservice.archchatmessagingservice.application.exceptions;

public class UserBlockedException extends RuntimeException{
    public UserBlockedException(String message){
        super(message);
    }
}
