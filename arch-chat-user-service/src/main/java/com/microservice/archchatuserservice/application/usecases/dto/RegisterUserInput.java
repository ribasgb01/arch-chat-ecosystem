package com.microservice.archchatuserservice.application.usecases.dto;

import java.time.LocalDate;

public record RegisterUserInput(
        String email,
        String password,
        String username,
        String nickname,
        LocalDate birthDate
) {}
