package com.microservice.archchatuserservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String password;
    private String username;
    private String nickname;
    private LocalDate birthDate;
    private boolean isEmailVerified;
    private Role role;

}
