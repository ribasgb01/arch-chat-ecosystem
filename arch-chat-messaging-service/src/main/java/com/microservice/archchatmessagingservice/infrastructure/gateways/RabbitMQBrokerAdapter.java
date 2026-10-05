package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.config.RabbitMQConfig;
import com.microservice.archchatmessagingservice.infrastructure.messaging.dto.NotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RabbitMQBrokerAdapter implements MessagePublisherGateway {

    private final RabbitTemplate rabbitTemplate;

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Override
    public void publishMessage(Message message) {

        String routingKey = "chat.message." + message.getChatId().toString();

        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, routingKey, message);
        System.out.println("Mensagem enviada com sucesso para o RabbitMQ na routing key: " + routingKey);
    }

    @Override
    public void publishNotification(NotificationEventDto notification){
        rabbitTemplate.convertAndSend(
                NOTIFICATION_EXCHANGE,
                "system.notifications.event",
                notification
        );
        System.out.println("Notificação do Sininho enviada para o RabbitMQ para o destinatário: " + notification.receiverId());
    }

}
