package com.microservice.archchatuserservice.controller.dto.response;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String username,
        String nickname,
        boolean isEmailVerified
) {}
