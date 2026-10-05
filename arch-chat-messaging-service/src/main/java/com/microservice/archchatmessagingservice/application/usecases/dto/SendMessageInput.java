package com.microservice.archchatmessagingservice.application.usecases.dto;

import com.microservice.archchatmessagingservice.domain.enums.MessageType;

import java.io.InputStream;
import java.util.UUID;

public record SendMessageInput(
        UUID chatId,
        UUID senderId,
        String content,
        MessageType type,
        InputStream fileStream,
        Long fileSize,
        String fileName,
        String contentType
) {}
