package com.microservice.archchatmessagingservice.application.usecases.dto;

import java.util.UUID;

public record BlockUserInput (
        UUID blockerId,
        UUID blockedId
) {}
