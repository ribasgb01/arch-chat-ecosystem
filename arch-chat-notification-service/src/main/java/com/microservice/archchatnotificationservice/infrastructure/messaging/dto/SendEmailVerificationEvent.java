package com.microservice.archchatnotificationservice.infrastructure.messaging.dto;

import java.io.Serializable;

public record SendEmailVerificationEvent(
        String email,
        String username,
        String verificationCode
) implements Serializable {
}
