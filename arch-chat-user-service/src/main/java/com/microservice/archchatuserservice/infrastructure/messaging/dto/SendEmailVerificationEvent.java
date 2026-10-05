package com.microservice.archchatuserservice.infrastructure.messaging.dto;

import java.io.Serializable;

public record SendEmailVerificationEvent (
        String email,
        String username,
        String verificationCode
) implements Serializable {}
