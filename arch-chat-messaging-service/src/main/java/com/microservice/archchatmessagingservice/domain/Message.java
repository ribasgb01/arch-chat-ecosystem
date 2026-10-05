package com.microservice.archchatmessagingservice.domain;

import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    UUID id;
    UUID chatId;
    UUID senderId;
    String content;
    LocalDateTime timestamp;
    boolean isEdited;
    Attachment attachment;
    MessageType type;
    MessageStatus status;
}
