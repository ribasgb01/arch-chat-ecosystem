package com.microservice.archchatuserservice.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @Email(message = "Formato de e-mail inválido")
        @NotBlank(message = "É necessário informar um email")
        String email,

        @NotBlank(message = "É necessário informar uma senha")
        String password
) {}
