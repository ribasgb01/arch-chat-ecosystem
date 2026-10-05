package com.microservice.archchatmessagingservice.infrastructure.persistence.entities;

import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDocument {

    @Id
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private List<UUID> participantIds;
    private ChatType type;
    private LastMessage lastMessage;
}
