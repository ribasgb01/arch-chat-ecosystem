package com.microservice.archchatuserservice.controller.dto.response;

import java.util.UUID;

public record UserSearchResponse (
        UUID id,
        String username,
        String nickname
){}
