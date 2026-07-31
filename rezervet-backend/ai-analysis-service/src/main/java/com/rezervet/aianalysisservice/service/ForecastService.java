package com.rezervet.aianalysisservice.service;

import com.rezervet.aianalysisservice.dto.response.ForecastDto;
import com.rezervet.aianalysisservice.dto.source.ReservationDto;
import com.rezervet.aianalysisservice.dto.source.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Level 2 tələb proqnozu: həftəlik mövsümilik (weekday üzrə orta) + ümumi orta fallback.
 */
@Service
@RequiredArgsConstructor
public class ForecastService {

    private final AnalyticsService analyticsService;

    public ForecastDto forecast(UUID restaurantId, int days) {
        List<ReservationDto> reservations = analyticsService.reservations(restaurantId).stream()
                .filter(r -> r.getDate() != null && r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        // Tarix -> say
        Map<LocalDate, Integer> perDate = new HashMap<>();
        for (ReservationDto r : reservations) perDate.merge(r.getDate(), 1, Integer::sum);

        // Weekday -> orta gündəlik say
        Map<DayOfWeek, List<Integer>> byWeekday = new EnumMap<>(DayOfWeek.class);
        for (var e : perDate.entrySet()) {
            byWeekday.computeIfAbsent(e.getKey().getDayOfWeek(), k -> new ArrayList<>()).add(e.getValue());
        }
        double overallAvg = perDate.isEmpty() ? 0
                : perDate.values().stream().mapToInt(Integer::intValue).average().orElse(0);

        List<ForecastDto.DayForecast> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= days; i++) {
            LocalDate d = today.plusDays(i);
            DayOfWeek wd = d.getDayOfWeek();
            List<Integer> hist = byWeekday.get(wd);
            double expected = (hist == null || hist.isEmpty())
                    ? overallAvg
                    : hist.stream().mapToInt(Integer::intValue).average().orElse(overallAvg);
            result.add(new ForecastDto.DayForecast(d, wd.name(), (int) Math.round(expected)));
        }
        return ForecastDto.builder().days(result).build();
    }
}
