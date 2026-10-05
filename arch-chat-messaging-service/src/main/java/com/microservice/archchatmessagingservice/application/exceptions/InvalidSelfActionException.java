package com.microservice.archchatmessagingservice.application.exceptions;

public class InvalidSelfActionException extends RuntimeException{

    public InvalidSelfActionException(String message){
        super(message);
    }
}

