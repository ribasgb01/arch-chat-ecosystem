package com.microservice.archchatcallingservice.infrastructure.controller;

import com.microservice.archchatcallingservice.infrastructure.config.UserAuthenticated;
import com.microservice.archchatcallingservice.infrastructure.messaging.dto.SignalMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class CallSignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call/signal")
    public void processSignal(@Payload SignalMessageDto signal, Principal principal) {
        UserAuthenticated caller = extractUser(principal);

        SignalMessageDto secureSignal = new SignalMessageDto(
                caller.id(),
                signal.receiverId(),
                signal.chatId(),
                signal.type(),
                signal.data(),
                LocalDateTime.now()
        );

        String destination = "/topic/call.user." + signal.receiverId();

        System.out.println("[SIGNAL " + signal.type() + "] De: " + caller.email() + " -> Para o Usuário: " + signal.receiverId());

        messagingTemplate.convertAndSend(destination, secureSignal);
    }

    private UserAuthenticated extractUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof UserAuthenticated user) {
                return user;
            }
        }
        throw new IllegalArgumentException("Usuário não autenticado no WebSocket de chamadas");
    }
}
