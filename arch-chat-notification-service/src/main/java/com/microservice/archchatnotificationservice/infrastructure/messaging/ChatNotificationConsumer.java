package com.microservice.archchatnotificationservice.infrastructure.messaging;

import com.microservice.archchatnotificationservice.application.NotificationUseCase;
import com.microservice.archchatnotificationservice.domain.Notification;
import com.microservice.archchatnotificationservice.infrastructure.config.rabbitmq.RabbitMQConfig;
import com.microservice.archchatnotificationservice.infrastructure.messaging.dto.NotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatNotificationConsumer {

    private final NotificationUseCase notificationUseCase;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_EVENTS_QUEUE)
    public void consumeNotificationEvent(NotificationEventDto event){

        Notification notification = Notification.builder()
                .senderId(event.senderId())
                .receiverId(event.receiverId())
                .chatId(event.chatId())
                .type(event.type())
                .content(event.content())
                .read(false)
                .timestamp(event.timestamp())
                .build();

        notificationUseCase.saveNotification(notification);
    }
}
