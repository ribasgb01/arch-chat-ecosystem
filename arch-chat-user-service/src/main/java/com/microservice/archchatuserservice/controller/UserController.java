package com.microservice.archchatuserservice.controller;

import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import com.microservice.archchatuserservice.application.usecases.SearchUsersUseCase;
import com.microservice.archchatuserservice.controller.dto.response.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SearchUsersUseCase searchUsersUseCase;
    private final UserRepositoryGateway userRepositoryGateway;

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam String query) {
        List<UserSearchResponse> results = searchUsersUseCase.search(query).stream()
                .map(u -> new UserSearchResponse(u.getId(), u.getUsername(), u.getNickname()))
                .toList();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSearchResponse> getUserById(@PathVariable UUID id) {
        return userRepositoryGateway.findById(id)
                .map(u -> new UserSearchResponse(u.getId(), u.getUsername(), u.getNickname()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
