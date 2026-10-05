package com.microservice.archchatmessagingservice.application.exceptions;

public class UnauthorizedActionException extends RuntimeException{
    public UnauthorizedActionException(String message){
        super(message);
    }
}
