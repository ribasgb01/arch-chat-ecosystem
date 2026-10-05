package com.microservice.archchatmessagingservice.infrastructure.persistence.mappers;

import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.MessageDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDocument toDocument(Message domain);
    Message toDomain(MessageDocument document);
}
