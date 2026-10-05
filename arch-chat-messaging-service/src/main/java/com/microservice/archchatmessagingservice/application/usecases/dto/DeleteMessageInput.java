package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record DeleteMessageInput(
        UUID messageId,
        UUID userId
) {
}
