package com.rezervet.reservationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
