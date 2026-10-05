package com.microservice.archchatuserservice.application.usecases;

import com.microservice.archchatuserservice.application.exceptions.InvalidTokenException;
import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import com.microservice.archchatuserservice.application.gateways.TokenProviderGateway;
import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import com.microservice.archchatuserservice.domain.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class RefreshTokenUseCase {

    private final TokenProviderGateway tokenProviderGateway;
    private final CacheGateway cacheGateway;
    private final UserRepositoryGateway userRepositoryGateway;

    public RefreshTokenUseCase(
            TokenProviderGateway tokenProviderGateway,
            CacheGateway cacheGateway,
            UserRepositoryGateway userRepositoryGateway){
        this.tokenProviderGateway = tokenProviderGateway;
        this.cacheGateway = cacheGateway;
        this.userRepositoryGateway = userRepositoryGateway;
    }

    public String refresh(String refreshToken){

        String email = tokenProviderGateway.validateToken(refreshToken);
        if (email == null){
           throw new InvalidTokenException("Refresh token inválido ou expirado");
        }

        String storedEmail = cacheGateway.get("refresh:" + refreshToken);

        if (storedEmail == null || !storedEmail.equals(email)){
            throw new InvalidTokenException("Sessão expirada. Faça login novamente");
        }

        User user = userRepositoryGateway.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return tokenProviderGateway.generateAccessToken(user);
    }
}
