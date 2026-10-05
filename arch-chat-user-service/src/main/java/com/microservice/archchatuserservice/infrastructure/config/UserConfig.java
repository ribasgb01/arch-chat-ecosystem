package com.microservice.archchatuserservice.infrastructure.config;

import com.microservice.archchatuserservice.application.gateways.*;
import com.microservice.archchatuserservice.application.usecases.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Value("${api.security.token.refresh-expiration}")
    private Long refreshExpiration;

    @Bean
    public RegisterUserUseCase registerUserUseCase (
            UserRepositoryGateway userRepositoryGateway,
            PasswordEncodeGateway passwordEncodeGateway,
            CacheGateway cacheGateway,
            EventPublisherGateway eventPublisherGateway
    ) {
        return new RegisterUserUseCase(userRepositoryGateway, passwordEncodeGateway, cacheGateway, eventPublisherGateway);
    }

    @Bean
    public AuthenticationUserUseCase authenticationUserUseCase(
            PasswordEncodeGateway passwordEncodeGateway,
            UserRepositoryGateway userRepositoryGateway,
            TokenProviderGateway tokenProviderGateway,
            CacheGateway cacheGateway
    ) {
        return new AuthenticationUserUseCase(passwordEncodeGateway, userRepositoryGateway, tokenProviderGateway, cacheGateway, refreshExpiration);
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            UserRepositoryGateway userRepositoryGateway,
            CacheGateway cacheGateway
    ) {
        return new VerifyEmailUseCase(userRepositoryGateway, cacheGateway);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            TokenProviderGateway tokenProviderGateway,
            CacheGateway cacheGateway,
            UserRepositoryGateway userRepositoryGateway
    ) {
        return new RefreshTokenUseCase(tokenProviderGateway, cacheGateway, userRepositoryGateway);
    }

    @Bean
    public LogoutUserUseCase logoutUserUseCase(CacheGateway cacheGateway, TokenProviderGateway tokenProviderGateway){
        return new LogoutUserUseCase(cacheGateway, tokenProviderGateway);
    }

    @Bean
    public SearchUsersUseCase searchUsersUseCase(UserRepositoryGateway userRepositoryGateway) {
        return new SearchUsersUseCase(userRepositoryGateway);
    }
}
