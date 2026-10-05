package com.microservice.archchatmessagingservice.controller.dto.request;

import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendMessageRequest(

        @NotNull
        String content,

        @NotNull
        MessageType type
) {}
