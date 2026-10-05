package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepositoryGateway {
    Chat save (Chat chat);
    Optional<Chat> findById(UUID id);
    Optional<Chat> findDirectChatBetween(UUID user1, UUID user2);
    List<Chat> findChatsByUserId(UUID userId);
    void deleteChat(UUID chatId);
}
