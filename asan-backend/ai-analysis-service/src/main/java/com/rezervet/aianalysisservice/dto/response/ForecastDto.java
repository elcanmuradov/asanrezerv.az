package com.rezervet.aianalysisservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// Level 2 tələb proqnozu (növbəti günlər)
@Data
@Builder
@AllArgsConstructor
public class ForecastDto {
    private List<DayForecast> days;

    @Data
    @Builder
    @AllArgsConstructor
    public static class DayForecast {
        private LocalDate date;
        private String weekday;      // MONDAY...
        private int expectedReservations;
    }
}
