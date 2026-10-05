package com.microservice.archchatmessagingservice.application.exceptions;

public class AlreadyAreFriendsException extends RuntimeException{
    
    public AlreadyAreFriendsException(String message){
        super(message);
    }
}
