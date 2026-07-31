package com.rezervet.aianalysisservice.service;

import com.rezervet.aianalysisservice.dto.response.AiSummaryDto;
import com.rezervet.aianalysisservice.dto.response.InsightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Level 2 tövsiyələr — tamamilə qayda-əsaslı (LLM YOX, bax: bax DeepInsightService/Level 3).
 */
@Service
@RequiredArgsConstructor
public class InsightService {

    private final AnalyticsService analyticsService;

    public InsightDto insights(UUID userId, UUID restaurantId) {
        AiSummaryDto s = analyticsService.computeSummary(userId, restaurantId);
        List<String> recommendations = buildRecommendations(s);
        String narrative = buildRuleBasedNarrative(s, recommendations);

        return InsightDto.builder()
                .narrative(narrative)
                .llmGenerated(false)
                .recommendations(recommendations)
                .build();
    }

    List<String> buildRecommendations(AiSummaryDto s) {
        List<String> rec = new ArrayList<>();

        // Peak saat
        peakHour(s).ifPresent(h -> rec.add(
                "Ən sıx saat " + h + ":00 — bu saatda əlavə masa/ofisiant planlaşdırın."));

        // No-show
        if (s.getNoShowRate() >= 0.2) {
            rec.add("İmtina/no-show dərəcəsi yüksəkdir (" + pct(s.getNoShowRate())
                    + ") — təsdiq SMS/xatırlatma və depozit siyasəti tətbiq edin.");
        }

        // Doluluq
        if (s.getTotalReservations() > 0 && s.getAvgOccupancyPercent() < 30) {
            rec.add("Orta doluluq aşağıdır (" + s.getAvgOccupancyPercent()
                    + "%) — sakit günlərə kampaniya/endirim düşünün.");
        } else if (s.getAvgOccupancyPercent() > 85) {
            rec.add("Doluluq çox yüksəkdir (" + s.getAvgOccupancyPercent()
                    + "%) — kapasiteni artırmaq və ya növbə/gözləmə siyahısı faydalı olar.");
        }

        // Kanal
        Integer manual = s.getChannelSplit().get("MANUAL");
        Integer online = s.getChannelSplit().get("ONLINE");
        if (manual != null && online != null && manual > online) {
            rec.add("Rezervlərin çoxu əl ilə (zəng) gəlir — onlayn rezervasiyanı tanıtmaq işçi yükünü azaldar.");
        }

        if (rec.isEmpty()) rec.add("Data kifayət qədər deyil — daha çox rezerv toplandıqca tövsiyələr dəqiqləşəcək.");
        return rec;
    }

    private String buildRuleBasedNarrative(AiSummaryDto s, List<String> rec) {
        StringBuilder sb = new StringBuilder();
        sb.append("Baxılan dövrdə ").append(s.getTotalReservations())
                .append(" rezerv qeydə alınıb. Orta doluluq ").append(s.getAvgOccupancyPercent())
                .append("%, imtina dərəcəsi ").append(pct(s.getNoShowRate())).append(".");
        peakHour(s).ifPresent(h -> sb.append(" Ən sıx saat ").append(h).append(":00-dır."));
        sb.append(" Əsas tövsiyələr: ");
        sb.append(String.join(" ", rec));
        return sb.toString();
    }

    private java.util.Optional<Integer> peakHour(AiSummaryDto s) {
        return s.getPeakHours().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    private String pct(double ratio) {
        return Math.round(ratio * 100) + "%";
    }
}
