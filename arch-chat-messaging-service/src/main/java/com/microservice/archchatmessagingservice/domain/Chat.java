package com.microservice.archchatmessagingservice.domain;

import com.microservice.archchatmessagingservice.domain.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat{
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private List<UUID> participantIds;
    private ChatType type;
    private LastMessage lastMessage;
}
