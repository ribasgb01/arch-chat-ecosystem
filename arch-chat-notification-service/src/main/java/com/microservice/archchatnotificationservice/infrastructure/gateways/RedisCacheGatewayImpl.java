package com.microservice.archchatnotificationservice.infrastructure.gateways;

import com.microservice.archchatnotificationservice.application.gateways.CacheGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheGatewayImpl implements CacheGateway {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean exists(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
