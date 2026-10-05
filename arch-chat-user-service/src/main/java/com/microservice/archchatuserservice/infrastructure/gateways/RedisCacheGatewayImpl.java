package com.microservice.archchatuserservice.infrastructure.gateways;

import com.microservice.archchatuserservice.application.gateways.CacheGateway;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisCacheGatewayImpl implements CacheGateway {

    private final StringRedisTemplate redisTemplate;

    public RedisCacheGatewayImpl(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, long expirationInMs) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(expirationInMs));
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);

    }
}
