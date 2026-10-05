package com.microservice.archchatuserservice.application.usecases;

import com.microservice.archchatuserservice.application.exceptions.InvalidCredentialsException;
import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import com.microservice.archchatuserservice.application.gateways.TokenProviderGateway;

public class LogoutUserUseCase {

    private final CacheGateway cacheGateway;
    private final TokenProviderGateway tokenProviderGateway;

    public LogoutUserUseCase (CacheGateway cacheGateway, TokenProviderGateway tokenProviderGateway){
        this.cacheGateway = cacheGateway;
        this.tokenProviderGateway = tokenProviderGateway;
    }

    public void logout(String accessToken, String refreshToken){

        String email = tokenProviderGateway.validateToken(accessToken);

        if (email == null){
            throw new InvalidCredentialsException("Token inválido");
        }

        long remainingTime = tokenProviderGateway.getRemainingTime(accessToken);

        if (remainingTime > 0){
            cacheGateway.set("blacklist:" + accessToken, "true", remainingTime);
        }

        if (refreshToken != null) {
            cacheGateway.delete("refresh:" + refreshToken);
        }
    }
}
