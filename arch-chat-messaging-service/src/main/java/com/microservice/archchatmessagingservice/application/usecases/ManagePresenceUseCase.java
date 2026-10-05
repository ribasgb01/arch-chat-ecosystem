package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.gateways.CacheGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.UserPresenceEvent;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import com.microservice.archchatmessagingservice.domain.enums.UserStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ManagePresenceUseCase {

    private final CacheGateway cacheGateway;
    private final MessagePublisherGateway messagePublisherGateway;

    private static final String SESSIONS_KEY_PREFIX = "presence:sessions:";

    public void userConnected(UUID userId, String sessionId){
        String key = SESSIONS_KEY_PREFIX + userId.toString();

        cacheGateway.addSetElement(key, sessionId);
        long activeSessions = cacheGateway.getSetSize(key);

        if(activeSessions == 1){
            UserPresenceEvent event =  new UserPresenceEvent(userId, UserStatus.ONLINE);
            publishPresenceGateway(event);
            System.out.println("Usuário online: " + userId);
        }
    }

    public void userDisconnected(UUID userId, String sessionId){
        String key = SESSIONS_KEY_PREFIX + userId.toString();

        cacheGateway.removeSetElement(key, sessionId);
        long activeSessions = cacheGateway.getSetSize(key);

        if(activeSessions == 0){
            cacheGateway.delete(key);
            UserPresenceEvent event = new UserPresenceEvent(userId, UserStatus.OFFLINE);
            publishPresenceGateway(event);
            System.out.println("Usuário offline: " + userId);
        }
    }

    private void publishPresenceGateway(UserPresenceEvent event){

        Message presenceMessage = Message.builder()
                .id(UUID.randomUUID())
                .chatId(event.userId())
                .senderId(event.userId())
                .content(event.status().name())
                .timestamp(LocalDateTime.now())
                .type(MessageType.SYSTEM)
                .status(MessageStatus.SENT)
                .build();

        messagePublisherGateway.publishMessage(presenceMessage);
    }
}
