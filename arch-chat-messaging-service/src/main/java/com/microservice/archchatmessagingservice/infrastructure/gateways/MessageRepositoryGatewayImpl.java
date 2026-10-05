package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.persistence.DataMessageRepository;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.MessageDocument;
import com.microservice.archchatmessagingservice.infrastructure.persistence.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryGatewayImpl implements MessageRepositoryGateway {

    private final MessageMapper mapper;
    private final DataMessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        return mapper.toDomain(
                messageRepository.save(mapper.toDocument(message))
        );
    }

    @Override
    public Page<Message> findMessagesByChatId(UUID chatId, Pageable pageable) {
        return messageRepository.findByChatIdOrderByTimestampDesc(chatId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Message> findAllMessagesByChatId(UUID chatId) {
        return messageRepository.findAllByChatId(chatId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(document -> mapper.toDomain(document));
    }
}
