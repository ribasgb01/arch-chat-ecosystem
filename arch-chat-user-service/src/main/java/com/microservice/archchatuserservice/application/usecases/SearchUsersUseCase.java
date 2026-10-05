package com.microservice.archchatuserservice.application.usecases;

import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import com.microservice.archchatuserservice.domain.User;

import java.util.List;

public class SearchUsersUseCase {

    private final UserRepositoryGateway userRepositoryGateway;

    public SearchUsersUseCase(UserRepositoryGateway userRepositoryGateway) {
        this.userRepositoryGateway = userRepositoryGateway;
    }

    public List<User> search(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return userRepositoryGateway.searchUsers(query.trim());
    }
}
