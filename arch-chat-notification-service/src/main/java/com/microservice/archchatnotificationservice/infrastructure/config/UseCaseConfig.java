package com.microservice.archchatnotificationservice.infrastructure.config;

import com.microservice.archchatnotificationservice.application.NotificationUseCase;
import com.microservice.archchatnotificationservice.application.gateways.NotificationRepositoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public NotificationUseCase notificationUseCase(NotificationRepositoryGateway notificationRepositoryGateway) {
        return new NotificationUseCase(notificationRepositoryGateway);
    }
}
