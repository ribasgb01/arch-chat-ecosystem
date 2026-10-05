package com.microservice.archchatmessagingservice.infrastructure.persistence.mappers;

import  com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.ChatDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    ChatDocument toDocument(Chat domain);
    Chat toDomain(ChatDocument document);

}
