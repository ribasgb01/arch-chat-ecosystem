package com.microservice.archchatuserservice.application.usecases.dto;

public record AuthenticationUserInput(
   String email,
   String password
) {}
