package com.rezervet.aianalysisservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

// Level 1 əsas statistika
@Data
@Builder
@AllArgsConstructor
public class AiSummaryDto {
    private int totalReservations;         // baxılan dövrdə ümumi rezerv
    private int totalCapacity;             // restoranın ümumi masa tutumu
    private double avgOccupancyPercent;    // orta doluluq %
    private Map<Integer, Integer> peakHours;      // saat(0-23) -> rezerv sayı
    private Map<String, Integer> channelSplit;    // ONLINE/MANUAL -> say
    private double noShowRate;             // (DENIED+EXPIRED)/total
    private List<MonthlyPoint> monthlyTrend;      // aylıq rezerv trendi

    // Dashboard metrics
    private int branchCount;
    private int tableCount;
    private int monthlyReservations;

    @Data
    @Builder
    @AllArgsConstructor
    public static class MonthlyPoint {
        private String label;  // "2026-07"
        private int count;
    }
}
