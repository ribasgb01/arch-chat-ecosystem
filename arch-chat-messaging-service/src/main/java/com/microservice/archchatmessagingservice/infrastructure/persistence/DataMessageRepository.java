package com.microservice.archchatmessagingservice.infrastructure.persistence;

import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.ChatDocument;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.MessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataMessageRepository extends MongoRepository<MessageDocument, UUID> {

    Page<MessageDocument> findByChatIdOrderByTimestampDesc(UUID chatId, Pageable pageable);

    List<MessageDocument> findAllByChatId(UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
