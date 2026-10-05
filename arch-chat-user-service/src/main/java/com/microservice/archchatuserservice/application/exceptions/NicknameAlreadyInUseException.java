package com.microservice.archchatuserservice.application.exceptions;

public class NicknameAlreadyInUseException extends RuntimeException{

    public NicknameAlreadyInUseException (String message){
        super(message);
    }
}
