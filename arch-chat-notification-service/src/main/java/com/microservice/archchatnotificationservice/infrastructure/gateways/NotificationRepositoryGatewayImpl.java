package com.microservice.archchatnotificationservice.infrastructure.gateways;

import com.microservice.archchatnotificationservice.application.gateways.NotificationRepositoryGateway;
import com.microservice.archchatnotificationservice.domain.Notification;
import com.microservice.archchatnotificationservice.infrastructure.persistence.DataNotificationRepository;
import com.microservice.archchatnotificationservice.infrastructure.persistence.entities.NotificationEntity;
import com.microservice.archchatnotificationservice.infrastructure.persistence.mappers.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryGatewayImpl implements NotificationRepositoryGateway {

    private final DataNotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Page<Notification> findByReceiverId(UUID receiverId, Pageable pageable) {
        return repository.findByReceiverIdOrderByTimestampDesc(receiverId, pageable)
                .map(mapper::toDomain);
        
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
