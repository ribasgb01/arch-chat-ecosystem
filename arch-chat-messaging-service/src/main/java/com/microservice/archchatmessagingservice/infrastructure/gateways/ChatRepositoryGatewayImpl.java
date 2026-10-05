package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.infrastructure.persistence.DataChatRepository;
import com.microservice.archchatmessagingservice.infrastructure.persistence.DataMessageRepository;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.ChatDocument;
import com.microservice.archchatmessagingservice.infrastructure.persistence.mappers.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatRepositoryGatewayImpl implements ChatRepositoryGateway {

    private final DataChatRepository chatRepository;
    private final DataMessageRepository messageRepository;
    private final ChatMapper mapper;

    @Override
    public Chat save(Chat chat) {
        return mapper.toDomain(chatRepository.save(
                mapper.toDocument(chat)
        ));
    }

    @Override
    public Optional<Chat> findById(UUID id) {
        return chatRepository.findById(id)
                .map(document -> mapper.toDomain(document));
    }

    @Override
    public Optional<Chat> findDirectChatBetween(UUID user1, UUID user2) {
        return chatRepository.findDirectChatBetween(user1, user2)
                .map(document -> mapper.toDomain(document));
    }

    @Override
    public List<Chat> findChatsByUserId(UUID userId) {
        return chatRepository.findAllByParticipantIdsContaining(userId)
                .stream()
                .map(document -> mapper.toDomain(document))
                .toList();

    }

    @Override
    public void deleteChat(UUID chatId) {
        chatRepository.deleteById(chatId);
        messageRepository.deleteAllByChatId(chatId);
    }
}
