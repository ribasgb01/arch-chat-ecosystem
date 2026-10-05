package com.microservice.archchatmessagingservice.infrastructure.persistence.entities;

import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDocument {

    @Id
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isEdited;
    private Attachment attachment;
    private MessageType type;
    private MessageStatus status;
}
