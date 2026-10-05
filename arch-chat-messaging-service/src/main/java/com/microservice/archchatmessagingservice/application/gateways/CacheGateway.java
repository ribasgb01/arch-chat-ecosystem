package com.microservice.archchatmessagingservice.application.gateways;

public interface CacheGateway {

    void set(String key, String value, long expirationInMs);

    String get(String key);

    boolean exists(String key);

    void delete(String key);

    void addSetElement(String key, String value);

    void removeSetElement(String key, String value);

    long getSetSize(String key);
}
