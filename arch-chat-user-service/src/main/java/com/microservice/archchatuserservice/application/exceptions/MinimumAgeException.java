package com.microservice.archchatuserservice.application.exceptions;

public class MinimumAgeException extends RuntimeException{
    public MinimumAgeException(String message) {
        super(message);
    }
}
