package com.microservice.archchatmessagingservice.infrastructure.config;

import com.microservice.archchatmessagingservice.application.usecases.ManagePresenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ManagePresenceUseCase presenceUseCase;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = headerAccessor.getUser();

        if(principal instanceof UsernamePasswordAuthenticationToken auth){
            if (auth.getPrincipal() instanceof UserAuthenticated user) {

                String sessionId = headerAccessor.getSessionId();
                presenceUseCase.userConnected(user.id(), sessionId);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = headerAccessor.getUser();

        if (principal instanceof UsernamePasswordAuthenticationToken auth){
            if (auth.getPrincipal() instanceof UserAuthenticated user){

                String sessionId = headerAccessor.getSessionId();
                presenceUseCase.userDisconnected(user.id(), sessionId);
            }
        }


    }
}
