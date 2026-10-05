package com.microservice.archchatuserservice.infrastructure.persistence.entities;

import com.microservice.archchatuserservice.domain.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private String password;
    private String username;
    private String nickname;
    private LocalDate birthDate;
    private boolean isEmailVerified;
    @Enumerated(EnumType.STRING)
    private Role role;
}
