package com.microservice.archchatuserservice.application.gateways;

import com.microservice.archchatuserservice.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryGateway {

    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    List<User> searchUsers(String query);
    Optional<User> findById(UUID id);
}
