package com.microservice.archchatnotificationservice.infrastructure.persistence;

import com.microservice.archchatnotificationservice.infrastructure.persistence.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DataNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Page<NotificationEntity> findByReceiverIdOrderByTimestampDesc(UUID receiverId, Pageable pageable);
}
