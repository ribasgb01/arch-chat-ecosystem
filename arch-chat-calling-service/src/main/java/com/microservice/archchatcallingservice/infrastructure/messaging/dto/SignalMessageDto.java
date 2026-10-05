package com.microservice.archchatcallingservice.infrastructure.messaging.dto;

import com.microservice.archchatcallingservice.domain.enums.SignalType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record SignalMessageDto (
        UUID senderId,
        UUID receiverId,
        UUID chatId,
        SignalType type,
        Object data,
        LocalDateTime timestamp
) implements Serializable {}
