package com.microservice.archchatuserservice.application.gateways;

import com.microservice.archchatuserservice.domain.User;

public interface TokenProviderGateway {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String validateToken(String token);

    Long getRemainingTime(String token);

}
