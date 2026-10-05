package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.messaging.dto.NotificationEventDto;

public interface MessagePublisherGateway {

    void publishMessage(Message message);
    void publishNotification(NotificationEventDto notification);
}
