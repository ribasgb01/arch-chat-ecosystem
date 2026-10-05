package com.microservice.archchatmessagingservice.infrastructure.persistence.mappers;

import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.FriendshipEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipEntity toEntity(Friendship domain);

    Friendship toDomain(FriendshipEntity entity);
}
