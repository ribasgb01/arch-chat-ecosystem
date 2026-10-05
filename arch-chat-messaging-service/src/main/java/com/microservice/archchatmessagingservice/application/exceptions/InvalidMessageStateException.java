package com.microservice.archchatmessagingservice.application.exceptions;

public class InvalidMessageStateException extends RuntimeException {
    public InvalidMessageStateException(String message){
        super(message);
    }
}
