package com.microservice.archchatmessagingservice.domain;

import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
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
public class LastMessage {
    UUID messageId;
    UUID senderId;
    String content;
    LocalDateTime timestamp;
    MessageStatus status;
}
