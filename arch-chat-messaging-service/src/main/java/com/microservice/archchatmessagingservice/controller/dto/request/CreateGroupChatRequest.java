package com.microservice.archchatmessagingservice.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CreateGroupChatRequest(
        @NotBlank(message = "O nome do grupo é obrigatório")
        String name,

        @NotEmpty(message = "O grupo precisa ter pelo menos um membro além de você")
        List<UUID> memberIds
) {}
