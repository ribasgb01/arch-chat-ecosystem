package com.microservice.archchatmessagingservice.controller.dto.receiver;

import java.util.List;

public record MessagePaginatedResponse(
        List<MessageResponse> content,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
