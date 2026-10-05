package com.microservice.archchatmessagingservice.infrastructure.messaging.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationEventDto(
        UUID senderId,
        UUID receiverId,
        UUID chatId,
        String type,
        String content,
        LocalDateTime timestamp
) implements Serializable {}
