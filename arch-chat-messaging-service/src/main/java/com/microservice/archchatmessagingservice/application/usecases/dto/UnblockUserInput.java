package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record UnblockUserInput(
        UUID unblockerId,
        UUID blockedId
) {}
