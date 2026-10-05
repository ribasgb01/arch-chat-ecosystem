package com.microservice.archchatuserservice.application.exceptions;

public class EmailAlreadyInUseException extends RuntimeException{

    public EmailAlreadyInUseException(String message){
        super(message);
    }
}
