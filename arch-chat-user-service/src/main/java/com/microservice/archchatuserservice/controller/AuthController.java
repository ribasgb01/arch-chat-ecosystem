package com.microservice.archchatuserservice.controller;

import com.microservice.archchatuserservice.application.usecases.AuthenticationUserUseCase;
import com.microservice.archchatuserservice.application.usecases.LogoutUserUseCase;
import com.microservice.archchatuserservice.application.usecases.RefreshTokenUseCase;
import com.microservice.archchatuserservice.application.usecases.dto.AuthenticationOutput;
import com.microservice.archchatuserservice.application.usecases.dto.AuthenticationUserInput;
import com.microservice.archchatuserservice.controller.dto.request.LoginRequest;
import com.microservice.archchatuserservice.controller.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationUserUseCase authenticationUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @Value("${api.security.token.refresh-expiration}")
    private Long refreshExpirationInMs;

    public AuthController(AuthenticationUserUseCase authenticationUserUseCase, RefreshTokenUseCase refreshTokenUseCase, LogoutUserUseCase logoutUserUseCase){
        this.authenticationUserUseCase = authenticationUserUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUserUseCase = logoutUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
        AuthenticationUserInput input = new AuthenticationUserInput(
                request.email(),
                request.password()
        );

        AuthenticationOutput output = authenticationUserUseCase.authentication(input);

        ResponseCookie cookie  = ResponseCookie.from("refreshToken", output.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(refreshExpirationInMs / 1000)
                .path("/api/auth/refresh")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponse(output.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authorizationHeader.substring(7);

        logoutUserUseCase.logout(token, refreshToken);

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0)
                .path("/api/auth/refresh")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh (@CookieValue(name = "refreshToken", required = false) String refreshToken){

        String newAccessToken = refreshTokenUseCase.refresh(refreshToken);

        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }

}
