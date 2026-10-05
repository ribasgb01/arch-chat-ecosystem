package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendAudioInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendMessageInput;
import com.microservice.archchatmessagingservice.controller.dto.receiver.MessagePaginatedResponse;
import com.microservice.archchatmessagingservice.controller.dto.receiver.MessageResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.SendAudioRequest;
import com.microservice.archchatmessagingservice.controller.dto.request.SendMessageRequest;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.config.UserAuthenticated;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageRestController {

    private final MessageUseCase messageUseCase;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal UserAuthenticated loggedInUser,
            @Valid @RequestPart("message") SendMessageRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        InputStream fileStream = null;
        Long fileSize = null;
        String fileName = null;
        String contentType = null;

        if (file != null && !file.isEmpty()){
            fileStream = file.getInputStream();
            fileSize = file.getSize();
            fileName = file.getOriginalFilename();
            contentType = file.getContentType();
        }

        SendMessageInput input = new SendMessageInput(
                chatId,
                loggedInUser.id(),
                request.content(),
                request.type(),
                fileStream,
                fileSize,
                fileName,
                contentType
        );

        Message savedMessage = messageUseCase.saveMessage(input);

        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                savedMessage.getChatId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.isEdited(),
                savedMessage.getAttachment(),
                savedMessage.getStatus(),
                savedMessage.getType()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/audio")
    public ResponseEntity<MessageResponse> sendAudio(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal UserAuthenticated loggedInUser,
            @RequestPart("audio") @Valid SendAudioRequest request,
            @RequestPart("file") MultipartFile file
            ) throws IOException {

        InputStream inputStream = file.getInputStream();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        SendAudioInput input = new SendAudioInput(
                chatId,
                loggedInUser.id(),
                inputStream,
                fileSize,
                fileName,
                contentType,
                request.duration()
        );

        Message savedMessage = messageUseCase.sendAudio(input);

        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                savedMessage.getChatId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.isEdited(),
                savedMessage.getAttachment(),
                savedMessage.getStatus(),
                savedMessage.getType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<MessagePaginatedResponse> getChatHistory(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal UserAuthenticated loggedInUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Page<Message> messagePage = messageUseCase.getChatHistory(chatId, loggedInUser.id(), page, size);

        List<MessageResponse> content = messagePage.getContent().stream()
                .map(messageDomain -> new MessageResponse(
                        messageDomain.getId(),
                        messageDomain.getChatId(),
                        messageDomain.getSenderId(),
                        messageDomain.getContent(),
                        messageDomain.getTimestamp(),
                        messageDomain.isEdited(),
                        messageDomain.getAttachment(),
                        messageDomain.getStatus(),
                        messageDomain.getType()
                )).toList();

        MessagePaginatedResponse response = new MessagePaginatedResponse(
                content,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{messageId}/attachment-url")
    public ResponseEntity<Map<String, String>> getAttachmentUrl(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @AuthenticationPrincipal UserAuthenticated loggedInUser
    ) {
        String url = messageUseCase.getAttachmentUrl(messageId, loggedInUser.id());
        return ResponseEntity.ok(Map.of("url", url));
    }
}
