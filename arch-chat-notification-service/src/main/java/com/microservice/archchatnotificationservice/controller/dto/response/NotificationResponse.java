package com.microservice.archchatnotificationservice.controller.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse (
        UUID id,
        UUID senderId,
        UUID receiverId,
        UUID chatId,
        String type,
        boolean read,
        String content,
        LocalDateTime timestamp
) {}
