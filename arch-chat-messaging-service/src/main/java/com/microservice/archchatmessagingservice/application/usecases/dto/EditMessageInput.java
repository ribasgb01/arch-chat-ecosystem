package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record EditMessageInput (
        UUID senderId,
        UUID messageId,
        String content
){}
