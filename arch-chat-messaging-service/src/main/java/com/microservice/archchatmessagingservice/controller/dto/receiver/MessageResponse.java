package com.microservice.archchatmessagingservice.controller.dto.receiver;

import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID chatId,
        UUID senderId,
        String content,
        LocalDateTime timestamp,
        boolean isEdited,
        Attachment attachment,
        MessageStatus status,
        MessageType type
) {}
