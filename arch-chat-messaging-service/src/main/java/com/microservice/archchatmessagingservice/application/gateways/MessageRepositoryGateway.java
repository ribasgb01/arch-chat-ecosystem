package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.MessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepositoryGateway {

    Message save(Message message);
    Page<Message> findMessagesByChatId(UUID chatId, Pageable pageable);
    List<Message> findAllMessagesByChatId(UUID chatId);
    Optional<Message> findById(UUID messageId);
}
