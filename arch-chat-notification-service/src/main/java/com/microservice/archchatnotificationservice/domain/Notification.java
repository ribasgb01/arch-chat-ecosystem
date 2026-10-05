package com.microservice.archchatnotificationservice.domain;

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
public class Notification {

    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private UUID chatId;
    private String type;
    private String content;
    private boolean read;
    private LocalDateTime timestamp;
}