package com.microservice.archchatuserservice.application.exceptions;

public class InvalidVerifyCodeException extends RuntimeException {
    public InvalidVerifyCodeException(String message) {
        super(message);
    }
}

