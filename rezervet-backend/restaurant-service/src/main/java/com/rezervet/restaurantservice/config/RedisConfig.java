package com.rezervet.restaurantservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
