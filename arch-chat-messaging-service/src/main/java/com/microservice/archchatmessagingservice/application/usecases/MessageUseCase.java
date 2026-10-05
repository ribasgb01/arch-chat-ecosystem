package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.exceptions.ChatNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.InvalidMessageStateException;
import com.microservice.archchatmessagingservice.application.exceptions.MessageNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.EditMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendAudioInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendMessageInput;
import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.domain.enums.ChatType;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import com.microservice.archchatmessagingservice.infrastructure.messaging.dto.NotificationEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MessageUseCase {

    private final ChatRepositoryGateway chatRepository;
    private final MessageRepositoryGateway messageRepository;
    private final FileStorageGateway fileStorage;
    private final MessagePublisherGateway publisherGateway;

    public Message saveMessage(SendMessageInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Attachment updatedAttachment = null;

        if(input.fileStream() != null){
            Attachment attachment = fileStorage.uploadFile(
                    input.fileStream(),
                    input.fileSize(),
                    input.fileName(),
                    input.contentType(),
                    input.chatId(),
                    null
            );

            String tempUrl = fileStorage.getPresignedUrl(attachment.getKey());

            updatedAttachment = new Attachment(
                    attachment.getId(),
                    attachment.getFileName(),
                    attachment.getContentType(),
                    attachment.getSize(),
                    attachment.getKey(),
                    tempUrl,
                    attachment.getDuration()
            );
        }

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(input.chatId())
                .senderId(input.senderId())
                .content(input.content())
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .type(input.type())
                .attachment(updatedAttachment)
                .isEdited(false)
                .build();

        Message savedMessage = messageRepository.save(message);

        List<UUID> recipients = chat.getParticipantIds().stream()
                .filter(id -> !id.equals(input.senderId()))
                .toList();

        for (UUID recipientId : recipients) {
            publisherGateway.publishNotification(new NotificationEventDto(
                    savedMessage.getSenderId(),
                    recipientId,
                    savedMessage.getChatId(),
                    "CHAT_MESSAGE",
                    chat.getType() == ChatType.GROUP
                            ? "Nova mensagem no grupo " + chat.getName()
                            : "Nova mensagem recebida no chat!",
                    LocalDateTime.now()
            ));
        }

        publisherGateway.publishMessage(savedMessage);

        LastMessage lastMessage = LastMessage.builder()
                .messageId(savedMessage.getId())
                .senderId(savedMessage.getSenderId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .status(savedMessage.getStatus())
                .build();

        chat.setLastMessage(lastMessage);
        chatRepository.save(chat);

        return savedMessage;
    }

    public Message sendAudio(SendAudioInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Attachment attachment = fileStorage.uploadFile(
                input.fileStream(),
                input.fileSize(),
                input.fileName(),
                input.contentType(),
                input.chatId(),
                input.duration()
        );

        String tempUrl = fileStorage.getPresignedUrl(attachment.getKey());

        Attachment updatedAttachment = new Attachment(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getContentType(),
                attachment.getSize(),
                attachment.getKey(),
                tempUrl,
                attachment.getDuration()
        );

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(input.chatId())
                .senderId(input.senderId())
                .content("Mensagem de voz")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .type(MessageType.AUDIO)
                .attachment(updatedAttachment)
                .isEdited(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        publisherGateway.publishMessage(savedMessage);

        LastMessage lastMessage = LastMessage.builder()
                .messageId(savedMessage.getId())
                .senderId(savedMessage.getSenderId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .status(savedMessage.getStatus())
                .build();

        chat.setLastMessage(lastMessage);

        chatRepository.save(chat);

        return savedMessage;
    }

    public Message editMessage(EditMessageInput input){

        Message message = messageRepository.findById(input.messageId())
                .orElseThrow(() -> new MessageNotFoundException("Mensagem não foi encontrada"));

        if(!message.getSenderId().equals(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para editar esta mensagem");
        }

        if(message.getStatus() == MessageStatus.DELETED){
            throw new InvalidMessageStateException("Esta mensagem já foi apagada");
        }

        message.setContent(input.content());
        message.setEdited(true);

        Message savedMessage = messageRepository.save(message);
        publisherGateway.publishMessage(savedMessage);

        return savedMessage;
    }

    public void deleteMessage(DeleteMessageInput input){

        Message message = messageRepository.findById(input.messageId())
                .orElseThrow(() -> new MessageNotFoundException("Mensagem não foi encontrada"));

        if(!message.getSenderId().equals(input.userId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para deletar esta mensagem");
        }

        if(message.getAttachment() != null){
            fileStorage.deleteFile(message.getAttachment().getKey());
            message.setAttachment(null);
        }

        message.setContent("🚫 Esta mensagem foi apagada");
        message.setEdited(true);

        Message updatedMessage = messageRepository.save(message);

        publisherGateway.publishMessage(updatedMessage);
    }

    public Page<Message> getChatHistory(UUID chatId, UUID userId, int page, int size){

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não encontrada"));

        if (!chat.getParticipantIds().contains(userId)) {
            throw new UnauthorizedActionException("Usuário não tem permissão para visualizar o histórico");
        }

        Pageable pageable = PageRequest.of(page, size);

        return messageRepository.findMessagesByChatId(chatId, pageable);
    }

    public String getAttachmentUrl(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Mensagem não encontrada"));

        Chat chat = chatRepository.findById(message.getChatId())
                .orElseThrow(() -> new ChatNotFoundException("Chat não encontrado"));

        if (!chat.getParticipantIds().contains(userId)) {
            throw new UnauthorizedActionException("Sem permissão para ver este anexo");
        }

        if (message.getAttachment() == null || message.getAttachment().getKey() == null) {
            throw new IllegalArgumentException("Esta mensagem não possui anexo");
        }

        return fileStorage.getPresignedUrl(message.getAttachment().getKey());
    }
}
