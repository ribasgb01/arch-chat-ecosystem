package com.microservice.archchatnotificationservice.controller;

import com.microservice.archchatnotificationservice.application.NotificationUseCase;
import com.microservice.archchatnotificationservice.controller.dto.response.NotificationPaginatedResponse;
import com.microservice.archchatnotificationservice.infrastructure.config.UserAuthenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    public ResponseEntity<NotificationPaginatedResponse> getUserNotifications(
            @AuthenticationPrincipal UserAuthenticated currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationUseCase.getRecentNotifications(currentUser.id(), page, size));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserAuthenticated currentUser
    ) {
        notificationUseCase.markAsRead(notificationId, currentUser.id());
        return ResponseEntity.noContent().build();
    }
}
