package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record AcceptFriendRequestInput (
        UUID friendshipId,
        UUID receiverId
){}
