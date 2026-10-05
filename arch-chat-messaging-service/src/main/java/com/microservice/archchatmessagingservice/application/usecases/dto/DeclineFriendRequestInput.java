package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record DeclineFriendRequestInput (
        UUID friendshipId,
        UUID receiverId
) {}
