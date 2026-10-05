package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.exceptions.ChatNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.FriendshipNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.domain.enums.ChatType;
import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ChatUseCase {

    private final ChatRepositoryGateway chatRepository;
    private final FriendshipRepositoryGateway friendshipRepository;
    private final MessageRepositoryGateway messageRepository;
    private final FileStorageGateway fileStorage;


    public Chat createChat(UUID user1, UUID user2){

        Friendship friendshipRelation = friendshipRepository.findRelationBetween(user1, user2)
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new FriendshipNotFoundException("Amizade não estabelecida para iniciar uma conversa"));

        Optional<Chat> existingChat = chatRepository.findDirectChatBetween(user1, user2);

        if(existingChat.isPresent()){
            return existingChat.get();
        }
        Chat newChat = new Chat(
                UUID.randomUUID(),
                null,
                LocalDateTime.now(),
                List.of(user1, user2),
                ChatType.DIRECT,
                null
        );

        return chatRepository.save(newChat);
    }

    public Chat createGroupChat(String name, List<UUID> memberIds, UUID creatorId) {
        List<UUID> allParticipants = new java.util.ArrayList<>(memberIds);
        if (!allParticipants.contains(creatorId)) {
            allParticipants.add(creatorId);
        }

        if (allParticipants.size() < 2) {
            throw new IllegalArgumentException("Um grupo deve ter pelo menos 2 participantes.");
        }

        Chat groupChat = new Chat(
                UUID.randomUUID(),
                name,
                LocalDateTime.now(),
                allParticipants,
                ChatType.GROUP,
                null
        );

        return chatRepository.save(groupChat);
    }

    public List<Chat> getChatsByUserId(UUID userId){

        List<Chat> userChats = chatRepository.findChatsByUserId(userId);

        return userChats.stream()
                .sorted((chat1, chat2) -> {
                    if(chat1.getLastMessage() == null){
                        return 1;
                    }

                    if(chat2.getLastMessage() == null){
                        return -1;
                    }

                    return chat2.getLastMessage().getTimestamp().compareTo(chat1.getLastMessage().getTimestamp());
                })
                .toList();
    }

    public void deleteChat(UUID userId, UUID chatId){

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não encontrada"));

        if (!chat.getParticipantIds().contains(userId)){
            throw new UnauthorizedActionException("Somente os usuários que pertencem ao chat podem deletar");
        }

        List<Message> messages = messageRepository.findAllMessagesByChatId(chatId);

        for (Message msg : messages) {
            if (msg.getAttachment() != null) {
                fileStorage.deleteFile(msg.getAttachment().getKey());
            }
        }

        chatRepository.deleteChat(chatId);
    }
}
