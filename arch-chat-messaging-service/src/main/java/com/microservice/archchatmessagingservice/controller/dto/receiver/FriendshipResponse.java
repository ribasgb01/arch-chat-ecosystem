package com.microservice.archchatmessagingservice.controller.dto.receiver;

import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID receiverId,
        FriendshipStatus status,
        LocalDateTime createdAt
) {}
