package com.microservice.archchatmessagingservice.controller.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EditMessageRequest (

        @NotNull
        UUID messageId,

        @NotNull
        String content
){}
