package com.microservice.archchatuserservice.application.gateways;

public interface PasswordEncodeGateway {

    String encode(String rawPassword);
    boolean matches (String rawPassword, String encodedPassword);

}
