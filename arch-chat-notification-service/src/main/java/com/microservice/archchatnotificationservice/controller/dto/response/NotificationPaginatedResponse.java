package com.microservice.archchatnotificationservice.controller.dto.response;

import java.util.List;

public record  NotificationPaginatedResponse (
        List<NotificationResponse> content,
        int currentPage,
        int totalPages,
        long totalElements
) {}
