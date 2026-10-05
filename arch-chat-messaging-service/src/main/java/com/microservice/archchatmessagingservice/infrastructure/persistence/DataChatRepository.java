package com.microservice.archchatmessagingservice.infrastructure.persistence;

import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataChatRepository extends MongoRepository<ChatDocument, UUID> {

    @Query("{ 'type': 'DIRECT', 'participantIds' : { '$all': [?0, ?1] } }")
    Optional<ChatDocument> findDirectChatBetween(UUID user1, UUID user2);
    List<ChatDocument> findAllByParticipantIdsContaining(UUID userId);
}
