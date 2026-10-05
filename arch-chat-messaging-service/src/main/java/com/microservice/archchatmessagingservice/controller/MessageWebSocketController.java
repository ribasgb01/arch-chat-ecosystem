package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.EditMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendMessageInput;
import com.microservice.archchatmessagingservice.controller.dto.request.EditMessageRequest;
import com.microservice.archchatmessagingservice.controller.dto.request.SendMessageRequest;
import com.microservice.archchatmessagingservice.infrastructure.config.UserAuthenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageUseCase messageUseCase;

    @MessageMapping("/chat/{chatId}/sendMessage")
    public void sendMessage(
            @DestinationVariable UUID chatId,
            @Payload SendMessageRequest request,
            Principal principal
            ){

        UserAuthenticated loggedInUser = extractUser(principal);
        SendMessageInput input = new SendMessageInput(
                chatId,
                loggedInUser.id(),
                request.content(),
                request.type(),
                null, null, null, null
        );

        messageUseCase.saveMessage(input);
    }

    @MessageMapping("/chat/{chatId}/deleteMessage")
    public void deleteMessage(
            @DestinationVariable UUID chatId,
            @Payload UUID messageId,
            Principal principal
        ){

        UserAuthenticated loggedInUser = extractUser(principal);
        DeleteMessageInput input = new DeleteMessageInput(
                messageId,
                loggedInUser.id()
        );

        messageUseCase.deleteMessage(input);
    }

    @MessageMapping("/chat/{chatId}/editMessage")
    public void editMessage(
            @DestinationVariable UUID chatId,
            @Payload EditMessageRequest request,
            Principal principal
        ){

        UserAuthenticated loggedInUser = extractUser(principal);
        EditMessageInput input = new EditMessageInput(
                loggedInUser.id(),
                request.messageId(),
                request.content()
        );

        messageUseCase.editMessage(input);
    }

    private UserAuthenticated extractUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof UserAuthenticated user) {
                return user;
            }
        }
        throw new UnauthorizedActionException("Usuário não autenticado no WebSocket");
    }
}
