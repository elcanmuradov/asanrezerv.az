package com.rezervet.aianalysisservice.service;

import com.rezervet.aianalysisservice.dto.response.AiSummaryDto;
import com.rezervet.aianalysisservice.dto.response.InsightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Level 3 — DeepSeek (LLM) ilə inteqrasiyalı dərin analiz. Key/enabled yoxdursa
 * Level 2-dəki qayda-əsaslı InsightService-in nəticəsinə keçir (llmGenerated=false).
 */
@Service
@RequiredArgsConstructor
public class DeepInsightService {

    private final AnalyticsService analyticsService;
    private final InsightService insightService;
    private final LlmClient llmClient;

    public InsightDto deepInsights(UUID userId, UUID restaurantId) {
        AiSummaryDto s = analyticsService.computeSummary(userId, restaurantId);
        List<String> recommendations = insightService.buildRecommendations(s);

        String narrative = null;
        boolean llm = false;
        if (llmClient.isEnabled()) {
            narrative = llmClient.generate(buildPrompt(s, recommendations));
            llm = narrative != null;
        }
        if (narrative == null) {
            // DeepSeek aktiv deyil və ya çağırış uğursuz oldu — Level 2-dəki qayda-əsaslı hesabata keç.
            return insightService.insights(userId, restaurantId);
        }

        return InsightDto.builder()
                .narrative(narrative)
                .llmGenerated(llm)
                .recommendations(recommendations)
                .build();
    }

    private String buildPrompt(AiSummaryDto s, List<String> rec) {
        return """
                Sən restoran analitika məsləhətçisisən. Aşağıdakı metriklərə əsasən Azərbaycan dilində,
                qısa (4-6 cümlə), praktiki və dərin bir hesabat yaz. Rəqəmləri şərh et, tendensiyaları
                izah et və konkret, prioritetləşdirilmiş addımlar təklif et.

                Metriklər:
                - Ümumi rezerv: %d
                - Ümumi masa tutumu: %d
                - Orta doluluq: %.2f%%
                - İmtina/no-show dərəcəsi: %.2f
                - Peak saatlar (saat->say): %s
                - Kanal bölgüsü: %s
                - Aylıq trend: %s

                Qayda-əsaslı tövsiyələr (istinad üçün): %s
                """.formatted(
                s.getTotalReservations(), s.getTotalCapacity(), s.getAvgOccupancyPercent(),
                s.getNoShowRate(), String.valueOf(s.getPeakHours()), String.valueOf(s.getChannelSplit()),
                String.valueOf(s.getMonthlyTrend()), String.join(" | ", rec));
    }
}
