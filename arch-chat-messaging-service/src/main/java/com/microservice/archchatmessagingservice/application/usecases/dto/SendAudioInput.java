package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.io.InputStream;
import java.util.UUID;

public record SendAudioInput(
        UUID chatId,
        UUID senderId,
        InputStream fileStream,
        Long fileSize,
        String fileName,
        String contentType,
        Double duration
) {
}
