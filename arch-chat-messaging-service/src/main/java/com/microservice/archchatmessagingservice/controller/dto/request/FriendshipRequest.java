package com.microservice.archchatmessagingservice.controller.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendshipRequest(

        @NotNull(message = "Campo não informado")
        UUID receiverId
) {}
