package com.microservice.archchatuserservice.application.gateways;

import com.microservice.archchatuserservice.infrastructure.messaging.dto.SendEmailVerificationEvent;

public interface EventPublisherGateway {
    void publishEmailVerification(SendEmailVerificationEvent event);
}
