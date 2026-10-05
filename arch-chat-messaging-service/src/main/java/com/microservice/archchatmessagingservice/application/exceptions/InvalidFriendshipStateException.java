package com.microservice.archchatmessagingservice.application.exceptions;

public class InvalidFriendshipStateException extends RuntimeException{
    public InvalidFriendshipStateException(String message){
        super(message);
    }
}
