package com.microservice.archchatmessagingservice.infrastructure.config;

import com.microservice.archchatmessagingservice.application.gateways.*;
import com.microservice.archchatmessagingservice.application.usecases.FriendshipUseCase;
import com.microservice.archchatmessagingservice.application.usecases.ManagePresenceUseCase;
import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservice.archchatmessagingservice.application.usecases.ChatUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public ChatUseCase chatUseCase(
        ChatRepositoryGateway chatRepositoryGateway,
        FriendshipRepositoryGateway friendshipRepositoryGateway,
        MessageRepositoryGateway messageRepositoryGateway,
        FileStorageGateway fileStorageGateway) {
            return new ChatUseCase(chatRepositoryGateway, friendshipRepositoryGateway, messageRepositoryGateway, fileStorageGateway);
        }

    @Bean
    public FriendshipUseCase sendFriendRequestUseCase(
            FriendshipRepositoryGateway friendshipRepositoryGateway,
            MessagePublisherGateway messagePublisherGateway){
        return new FriendshipUseCase(friendshipRepositoryGateway, messagePublisherGateway);
    }

    @Bean
    public MessageUseCase messageUseCase(
            ChatRepositoryGateway chatRepositoryGateway,
            MessageRepositoryGateway messageRepositoryGateway,
            FileStorageGateway fileStorageGateway,
            MessagePublisherGateway messagePublisherGateway
    ) {
        return new MessageUseCase(chatRepositoryGateway, messageRepositoryGateway, fileStorageGateway, messagePublisherGateway);
    }

    @Bean
    public ManagePresenceUseCase managePresenceUseCase(
            CacheGateway cacheGateway,
            MessagePublisherGateway messagePublisherGateway
    ){
        return new ManagePresenceUseCase(cacheGateway, messagePublisherGateway);
    }

}
