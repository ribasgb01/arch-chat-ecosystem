package com.microservice.archchatmessagingservice.controller.dto.receiver;

import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.enums.ChatType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChatResponse (
        UUID id,
        String name,
        LocalDateTime createdAt,
        List<UUID> participantIds,
        ChatType type,
        LastMessage lastMessage
) {}
