package com.microservice.archchatmessagingservice.application.usecases.dto;

import com.microservice.archchatmessagingservice.domain.enums.UserStatus;

import java.util.UUID;

public record UserPresenceEvent(
        UUID userId,
        UserStatus status
) {
}
