package com.microservice.archchatuserservice.application.usecases.dto;

public record AuthenticationOutput(
        String accessToken,
        String refreshToken
) {}
