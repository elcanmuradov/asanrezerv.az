package com.rezervet.aianalysisservice.controller;

import com.rezervet.aianalysisservice.dto.ApiResponse;
import com.rezervet.aianalysisservice.dto.response.AiAccessDto;
import com.rezervet.aianalysisservice.dto.response.AiSummaryDto;
import com.rezervet.aianalysisservice.dto.response.ForecastDto;
import com.rezervet.aianalysisservice.dto.response.InsightDto;
import com.rezervet.aianalysisservice.service.AiLevelService;
import com.rezervet.aianalysisservice.service.AnalyticsService;
import com.rezervet.aianalysisservice.service.DeepInsightService;
import com.rezervet.aianalysisservice.service.ForecastService;
import com.rezervet.aianalysisservice.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bütün endpoint-lər /api/ai — gateway yalnız MANAGER rolunu buraxır (ROLE_RULES).
 * Kimlik X-User-Id header-dən; restaurantId + səviyyə server tərəfdə həll olunur.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final AiLevelService levelService;
    private final AnalyticsService analyticsService;
    private final ForecastService forecastService;
    private final InsightService insightService;
    private final DeepInsightService deepInsightService;

    @GetMapping("/access")
    public ResponseEntity<ApiResponse<AiAccessDto>> access(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        int level = levelService.getLevel(restaurantId);

        List<String> features = new ArrayList<>();
        if (level >= 1) {
            features.add("occupancy");
            features.add("peakHours");
            features.add("channelSplit");
            features.add("noShow");
            features.add("monthlyTrend");
        }
        if (level >= 2) {
            features.add("forecast");
            features.add("recommendations");
            features.add("growth");
        }
        if (level >= 3) {
            features.add("llmInsights");
        }

        return ok(AiAccessDto.builder()
                .level(level)
                .canBasic(level >= 1)
                .canAdvanced(level >= 2)
                .canDeep(level >= 3)
                .features(features)
                .build());
    }

    // Level >= 1
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AiSummaryDto>> summary(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        levelService.requireLevel(levelService.getLevel(restaurantId), 1);
        return ok(analyticsService.computeSummary(userId, restaurantId));
    }

    @GetMapping("/reservations-monthly")
    public ResponseEntity<ApiResponse<List<AiSummaryDto.MonthlyPoint>>> reservationsMonthly(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        levelService.requireLevel(levelService.getLevel(restaurantId), 1);
        return ok(analyticsService.computeSummary(userId, restaurantId).getMonthlyTrend());
    }

    // Level >= 2
    @GetMapping("/forecast")
    public ResponseEntity<ApiResponse<ForecastDto>> forecast(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        levelService.requireLevel(levelService.getLevel(restaurantId), 2);
        return ok(forecastService.forecast(userId,restaurantId, 7));
    }

    // Level >= 2 (qayda-əsaslı tövsiyələr, LLM YOX)
    @GetMapping("/insights")
    public ResponseEntity<ApiResponse<InsightDto>> insights(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        levelService.requireLevel(levelService.getLevel(restaurantId), 2);
        return ok(insightService.insights(userId, restaurantId));
    }

    // Level >= 3 (DeepSeek ilə dərin analiz)
    @GetMapping("/deep-insights")
    public ResponseEntity<ApiResponse<InsightDto>> deepInsights(@RequestHeader("X-User-Id") UUID userId) {
        UUID restaurantId = levelService.resolveRestaurantId(userId);
        levelService.requireLevel(levelService.getLevel(restaurantId), 3);
        return ok(deepInsightService.deepInsights(userId, restaurantId));
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(T body) {
        return ResponseEntity.ok(ApiResponse.success(body));
    }
}
