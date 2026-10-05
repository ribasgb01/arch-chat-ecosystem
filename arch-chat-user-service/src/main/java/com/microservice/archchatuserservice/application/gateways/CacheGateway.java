package com.microservice.archchatuserservice.application.gateways;

public interface CacheGateway {

    void set(String key, String value, long expirationInMs);

    String get(String key);

    boolean exists(String key);

    void delete(String key);
}
