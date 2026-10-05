package com.microservice.archchatnotificationservice.application.gateways;

public interface CacheGateway {
    boolean exists(String key);
}
