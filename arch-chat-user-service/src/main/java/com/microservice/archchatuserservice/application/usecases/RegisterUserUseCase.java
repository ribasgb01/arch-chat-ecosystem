package com.microservice.archchatuserservice.application.usecases;

import com.microservice.archchatuserservice.application.exceptions.EmailAlreadyInUseException;
import com.microservice.archchatuserservice.application.exceptions.MinimumAgeException;
import com.microservice.archchatuserservice.application.exceptions.NicknameAlreadyInUseException;
import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import com.microservice.archchatuserservice.application.gateways.EventPublisherGateway;
import com.microservice.archchatuserservice.application.gateways.PasswordEncodeGateway;
import com.microservice.archchatuserservice.application.gateways.UserRepositoryGateway;
import com.microservice.archchatuserservice.application.usecases.dto.RegisterUserInput;
import com.microservice.archchatuserservice.domain.Role;
import com.microservice.archchatuserservice.domain.User;
import com.microservice.archchatuserservice.infrastructure.messaging.dto.SendEmailVerificationEvent;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepositoryGateway userRepositoryGateway;
    private final PasswordEncodeGateway passwordEncodeGateway;
    private final CacheGateway cacheGateway;
    private final EventPublisherGateway eventPublisherGateway;

    private static final long VERIFICATION_CODE_EXPIRATION_MS = 10 * 60 * 1000L;

    public User register(RegisterUserInput input){

        int age = Period.between(input.birthDate(), LocalDate.now()).getYears();

        if (age < 18){
            throw new MinimumAgeException("Você precisa ter pelo menos 18 anos para se cadastrar");
        }

        Optional<User> user = userRepositoryGateway.findByEmail(input.email());

        if (user.isPresent()){
            throw new EmailAlreadyInUseException("E-mail já está em uso.");
        }

        user = userRepositoryGateway.findByNickname(input.nickname());

        if (user.isPresent()){
            throw new NicknameAlreadyInUseException("Nickname já está em uso.");
        }

        String hashedPassword = passwordEncodeGateway.encode(input.password());

        User newUser = User.builder()
                .email(input.email())
                .password(hashedPassword)
                .username(input.username())
                .nickname(input.nickname())
                .birthDate(input.birthDate())
                .isEmailVerified(false)
                .role(Role.USER)
                .build();

        User savedUser = userRepositoryGateway.save(newUser);

        String verificationCode = generateVerificationCode();

        cacheGateway.set("verification:code:" + savedUser.getEmail(), verificationCode, VERIFICATION_CODE_EXPIRATION_MS);

        eventPublisherGateway.publishEmailVerification(
                new SendEmailVerificationEvent(savedUser.getEmail(), savedUser.getUsername(), verificationCode)
        );

        return savedUser;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Garante 6 dígitos (de 100000 a 999999)
        return String.valueOf(code);
    }
}
