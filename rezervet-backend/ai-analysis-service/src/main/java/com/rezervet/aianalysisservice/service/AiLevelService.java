package com.rezervet.aianalysisservice.service;

import com.rezervet.aianalysisservice.client.RestaurantClient;
import com.rezervet.aianalysisservice.client.SubscriptionClient;
import com.rezervet.aianalysisservice.dto.source.RestaurantDto;
import com.rezervet.aianalysisservice.exception.AiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Kimlik + səviyyə qapısı:
 *  - restaurantId-ni X-User-Id-dən SERVER tərəfdə həll edir (client-ə etibar yox).
 *  - aiAnalysisLevel-i subscription-service-dən alır (Redis-də qısa cache).
 */
@Service
@RequiredArgsConstructor
public class AiLevelService {

    private final RestaurantClient restaurantClient;
    private final SubscriptionClient subscriptionClient;
    private final StringRedisTemplate redis;

    private static final Duration LEVEL_TTL = Duration.ofMinutes(5);

    public UUID resolveRestaurantId(UUID userId) {
        RestaurantDto r = unwrap(restaurantClient.getMyRestaurant(userId));
        if (r == null || r.getId() == null) {
            throw new AiException(HttpStatus.NOT_FOUND, "Restoran tapılmadı — əvvəlcə restoranınızı yaradın.");
        }
        return r.getId();
    }

    public int getLevel(UUID restaurantId) {
        String key = "ai:level_" + restaurantId;
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try { return Integer.parseInt(cached); } catch (NumberFormatException ignored) {}
        }
        Integer level = unwrapInt(subscriptionClient.getAiLevel(restaurantId));
        int value = level == null ? 0 : level;
        redis.opsForValue().set(key, String.valueOf(value), LEVEL_TTL);
        return value;
    }

    public void requireLevel(int level, int min) {
        if (level < min) {
            throw new AiException(HttpStatus.FORBIDDEN,
                    "Bu funksiya üçün daha yüksək abunə paketi lazımdır.");
        }
    }

    private <T> T unwrap(com.rezervet.aianalysisservice.dto.ApiResponse<T> resp) {
        return resp == null ? null : resp.getData();
    }

    private Integer unwrapInt(com.rezervet.aianalysisservice.dto.ApiResponse<Integer> resp) {
        return resp == null ? null : resp.getData();
    }
}
