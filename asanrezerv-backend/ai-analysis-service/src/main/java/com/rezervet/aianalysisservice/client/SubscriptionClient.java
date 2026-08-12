package com.rezervet.aianalysisservice.client;

import com.rezervet.aianalysisservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "subscription-service", url = "${spring.client.urls.subscription-service}/api/subscriptions")
public interface SubscriptionClient {

    // subscription-service-ə əlavə olunmalı: GET /api/subscriptions/ai-level?id={restaurantId}
    // Aktiv abunə yoxdursa 0, əks halda plan.aiAnalysisLevel (null -> 0).
    @GetMapping("/ai-level")
    ApiResponse<Integer> getAiLevel(@RequestParam("id") UUID restaurantId);
}
