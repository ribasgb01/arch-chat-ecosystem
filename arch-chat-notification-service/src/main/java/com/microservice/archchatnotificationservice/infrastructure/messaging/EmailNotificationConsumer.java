package com.microservice.archchatnotificationservice.infrastructure.messaging;

import com.microservice.archchatnotificationservice.infrastructure.config.rabbitmq.RabbitMQConfig;
import com.microservice.archchatnotificationservice.infrastructure.mail.EmailService;
import com.microservice.archchatnotificationservice.infrastructure.messaging.dto.SendEmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_VERIFICATION_QUEUE)
    public void consumeEmailVerificationEvent(SendEmailVerificationEvent event){
        System.out.println("Evento de verificação de e-mail recebido do RabbitMQ para: " + event.email());

        emailService.sendVerificationCodeEmail(
                event.email(),
                event.username(),
                event.verificationCode()
        );
    }
}
