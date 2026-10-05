package com.microservice.archchatcallingservice.application.gateways;

public interface CacheGateway {
    boolean exists(String key);
}
