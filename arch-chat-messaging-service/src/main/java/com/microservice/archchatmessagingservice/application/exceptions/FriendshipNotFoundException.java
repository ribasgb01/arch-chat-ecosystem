package com.microservice.archchatmessagingservice.application.exceptions;

public class FriendshipNotFoundException extends RuntimeException{
    public FriendshipNotFoundException(String message){
        super(message);
    }
}
