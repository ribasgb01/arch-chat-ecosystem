package com.microservice.archchatuserservice.controller;

import com.microservice.archchatuserservice.application.usecases.RegisterUserUseCase;
import com.microservice.archchatuserservice.application.usecases.VerifyEmailUseCase;
import com.microservice.archchatuserservice.application.usecases.dto.RegisterUserInput;
import com.microservice.archchatuserservice.application.usecases.dto.VerifyEmailInput;
import com.microservice.archchatuserservice.controller.dto.request.RegisterRequest;
import com.microservice.archchatuserservice.controller.dto.request.VerifyEmailRequest;
import com.microservice.archchatuserservice.controller.dto.response.RegisterResponse;
import com.microservice.archchatuserservice.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterUserUseCase registerUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register (@Valid @RequestBody RegisterRequest request){

        RegisterUserInput input = new RegisterUserInput(
                request.email(),
                request.password(),
                request.username(),
                request.nickname(),
                request.birthDate()
        );

        User savedUser = registerUserUseCase.register(input);

        RegisterResponse response = new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getNickname(),
                savedUser.isEmailVerified()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailUseCase.verify(new VerifyEmailInput(request.email(), request.code()));
        return ResponseEntity.ok().build();
    }

}
