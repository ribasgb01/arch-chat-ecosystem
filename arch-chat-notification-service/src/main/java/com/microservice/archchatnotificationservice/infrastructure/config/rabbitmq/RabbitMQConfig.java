package com.microservice.archchatnotificationservice.infrastructure.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    public static final String EMAIL_VERIFICATION_QUEUE = "email.verification.queue";
    public static final String EMAIL_VERIFICATION_ROUTING_KEY = "email.verification";

    public static final String NOTIFICATION_EVENTS_QUEUE = "notification.events.queue";
    public static final String NOTIFICATION_EVENTS_ROUTING_KEY = "system.notifications.#";

    @Bean
    public TopicExchange notificationExchange(){
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue emailVerificationQueue(){
        return QueueBuilder.durable(EMAIL_VERIFICATION_QUEUE).build();
    }

    @Bean
    public Binding emailVerificationBinding(){
        return BindingBuilder
                .bind(emailVerificationQueue())
                .to(notificationExchange())
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public Queue notificationEventsQueue() {
        return QueueBuilder.durable(NOTIFICATION_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding notificationEventsBinding() {
        return BindingBuilder
                .bind(notificationEventsQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_EVENTS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
