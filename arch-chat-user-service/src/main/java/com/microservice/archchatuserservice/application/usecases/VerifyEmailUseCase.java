package com.microservice.archchatuserservice.application.usecases;

import com.microservice.archchatuserservice.application.exceptions.InvalidVerifyCodeException;
import com.microservice.archchatuserservice.application.exceptions.UserNotFoundException;
import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import com.microservice.archchatuserservice.application.usecases.dto.VerifyEmailInput;
import com.microservice.archchatuserservice.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerifyEmailUseCase {

    private final UserRepositoryGateway userRepositoryGateway;
    private final CacheGateway cacheGateway;

    public void verify(VerifyEmailInput input){

        String storedCode = cacheGateway.get("verification:code:" + input.email());

        if(storedCode == null || !storedCode.equals(input.code().trim())){
            throw new InvalidVerifyCodeException("Código de verificação inválido ou expirado");
        }

        User user = userRepositoryGateway.findByEmail(input.email())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        user.setEmailVerified(true);
        userRepositoryGateway.save(user);

        cacheGateway.delete("verification:code:" + input.email());
        System.out.println("E-mail verificado com sucesso para: " + input.email());
    }
}
