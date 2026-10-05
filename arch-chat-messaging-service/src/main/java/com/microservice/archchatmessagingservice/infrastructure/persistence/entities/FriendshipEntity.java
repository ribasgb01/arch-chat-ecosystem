package com.microservice.archchatmessagingservice.infrastructure.persistence.entities;

import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_id", "receiver_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime createdAt;
    private UUID requesterId;
    private UUID receiverId;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
    private boolean blockedByReceiver;
    private boolean blockedByRequester;

}
