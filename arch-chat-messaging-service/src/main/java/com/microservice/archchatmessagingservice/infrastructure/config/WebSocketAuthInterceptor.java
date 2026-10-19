package com.microservice.archchatmessagingservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    if (jwtService.isValid(token)) {
                        UUID userId = jwtService.extractUserId(token);
                        String email = jwtService.extractEmail(token);
                        String role = jwtService.extractRole(token);

                        UserAuthenticated user = new UserAuthenticated(userId, email, role);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        accessor.setUser(auth);
                        return message;
                    }
                }

                throw new AccessDeniedException("Conexão WebSocket rejeitada: token ausente ou inválido.");
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String destination = accessor.getDestination();

                if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth &&
                        auth.getPrincipal() instanceof UserAuthenticated user) {

                    if (destination != null && destination.startsWith("/topic/call.user.")) {
                        String targetUserId = destination.replace("/topic/call.user.", "");
                        if (!user.id().toString().equalsIgnoreCase(targetUserId)) {
                            throw new AccessDeniedException("Não autorizado a subscrever no canal deste usuário.");
                        }
                    }
                } else {
                    throw new AccessDeniedException("Acesso negado para subscrição anônima.");
                }
            }
        }

        return message;
    }
}