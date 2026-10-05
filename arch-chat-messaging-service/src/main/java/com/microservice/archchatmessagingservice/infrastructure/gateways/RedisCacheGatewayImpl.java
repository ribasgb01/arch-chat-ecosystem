package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.CacheGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCacheGatewayImpl implements CacheGateway {

    private final StringRedisTemplate redisTemplate;

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

    @Override
    public void addSetElement(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public void removeSetElement(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public long getSetSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0L;
    }
}
