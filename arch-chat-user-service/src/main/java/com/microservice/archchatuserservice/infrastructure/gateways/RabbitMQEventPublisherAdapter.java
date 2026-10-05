package com.microservice.archchatuserservice.infrastructure.gateways;

import com.microservice.archchatuserservice.application.gateways.EventPublisherGateway;
import com.microservice.archchatuserservice.infrastructure.config.RabbitMQConfig;
import com.microservice.archchatuserservice.infrastructure.messaging.dto.SendEmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisherAdapter implements EventPublisherGateway {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEmailVerification(SendEmailVerificationEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY,
                event
        );

        System.out.println("Evento de verificação de e-mail publicado no RabbitMQ para: " + event.email());
    }
}
