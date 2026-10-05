package com.microservice.archchatmessagingservice.application.exceptions;

public class RequestAlreadyPendingException extends RuntimeException{

    public RequestAlreadyPendingException(String message){
        super(message);
    }
}
