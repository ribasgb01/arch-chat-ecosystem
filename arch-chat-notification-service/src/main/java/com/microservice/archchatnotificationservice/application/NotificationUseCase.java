package com.microservice.archchatnotificationservice.application;

import com.microservice.archchatnotificationservice.application.exceptions.ForbiddenActionException;
import com.microservice.archchatnotificationservice.application.exceptions.NotificationNotFoundException;
import com.microservice.archchatnotificationservice.application.gateways.NotificationRepositoryGateway;
import com.microservice.archchatnotificationservice.controller.dto.response.NotificationPaginatedResponse;
import com.microservice.archchatnotificationservice.controller.dto.response.NotificationResponse;
import com.microservice.archchatnotificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationRepositoryGateway notificationGateway;

    public Notification saveNotification(Notification notification){
        notification.setRead(false);
        if (notification.getTimestamp() == null){
            notification.setTimestamp(LocalDateTime.now());
        }

        return notificationGateway.save(notification);
    }

    public NotificationPaginatedResponse getRecentNotifications(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notificationPage = notificationGateway.findByReceiverId(userId, pageable);

        List<NotificationResponse> responses = notificationPage.getContent()
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getSenderId(),
                        n.getReceiverId(),
                        n.getChatId(),
                        n.getType(),
                        n.isRead(),
                        n.getContent(),
                        n.getTimestamp()
                ))
                .toList();

        return new NotificationPaginatedResponse(
                responses,
                notificationPage.getNumber(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    public void markAsRead(UUID notificationId, UUID userId){
        Notification notification = notificationGateway.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notificação não encontrada"));

        if(!notification.getReceiverId().equals(userId)){
            throw new ForbiddenActionException("Você não tem permissão para marcar esta notificação como lida");
        }

        notification.setRead(true);
        notificationGateway.save(notification);
    }


}
