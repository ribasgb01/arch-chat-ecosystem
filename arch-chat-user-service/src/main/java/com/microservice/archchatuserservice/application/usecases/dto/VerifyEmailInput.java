package com.microservice.archchatuserservice.application.usecases.dto;

public record VerifyEmailInput(
        String email,
        String code
) {
}
