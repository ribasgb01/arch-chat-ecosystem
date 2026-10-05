package com.microservice.archchatnotificationservice.infrastructure.persistence.mappers;

import com.microservice.archchatnotificationservice.domain.Notification;
import com.microservice.archchatnotificationservice.infrastructure.persistence.entities.NotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationEntity toEntity(Notification domain);
    Notification toDomain(NotificationEntity entity);
}
