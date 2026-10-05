package com.microservice.archchatmessagingservice.domain;

import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;
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
public class Friendship {

    private UUID id;
    private LocalDateTime createdAt;
    private UUID requesterId;
    private UUID receiverId;
    private FriendshipStatus status;
    private boolean blockedByRequester;
    private boolean blockedByReceiver;
}
