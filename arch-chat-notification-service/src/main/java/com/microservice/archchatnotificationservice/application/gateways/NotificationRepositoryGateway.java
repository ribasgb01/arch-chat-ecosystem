package com.microservice.archchatnotificationservice.application.gateways;

import com.microservice.archchatnotificationservice.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryGateway {

    Notification save(Notification notification);
    Page<Notification> findByReceiverId (UUID receiverId, Pageable pageable);
    Optional<Notification> findById(UUID id);
}
