package com.rezervet.aianalysisservice.service;

import com.rezervet.aianalysisservice.client.ReservationClient;
import com.rezervet.aianalysisservice.client.RestaurantClient;
import com.rezervet.aianalysisservice.dto.ApiResponse;
import com.rezervet.aianalysisservice.dto.response.AiSummaryDto;
import com.rezervet.aianalysisservice.dto.source.BranchDto;
import com.rezervet.aianalysisservice.dto.source.ReservationDto;
import com.rezervet.aianalysisservice.dto.source.ReservationStatus;
import com.rezervet.aianalysisservice.dto.source.TableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reservation + restaurant datasından statistik metriklər (Level 1).
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ReservationClient reservationClient;
    private final RestaurantClient restaurantClient;

    private static final Set<ReservationStatus> NO_SHOW =
            EnumSet.of(ReservationStatus.NO_SHOW, ReservationStatus.CANCELLED);

    public List<ReservationDto> reservations(UUID restaurantId) {
        List<ReservationDto> list = data(reservationClient.getByRestaurant(restaurantId));
        return list == null ? List.of() : list;
    }

    public AiSummaryDto computeSummary(UUID userId, UUID restaurantId) {
        List<ReservationDto> reservations = reservations(restaurantId);

        // --- Tutum: filiallar -> masalar -> capacity cəmi ---
        int totalCapacity = 0;
        int totalTables = 0;
        List<BranchDto> branches = data(restaurantClient.getBranches(restaurantId));
        if (branches != null) {
            for (BranchDto b : branches) {
                List<TableDto> tables = data(restaurantClient.getTables(userId, b.getId()));
                if (tables != null) {
                    totalTables += tables.size();
                    totalCapacity += tables.stream().mapToInt(TableDto::getCapacity).sum();
                }
            }
        }

        int total = reservations.size();

        // Peak saatlar (startTime hour)
        Map<Integer, Integer> peakHours = new TreeMap<>();
        for (ReservationDto r : reservations) {
            if (r.getStartTime() != null) {
                peakHours.merge(r.getStartTime().getHour(), 1, Integer::sum);
            }
        }

        // Kanal bölgüsü
        Map<String, Integer> channel = new HashMap<>();
        for (ReservationDto r : reservations) {
            String key = r.getSource() == null ? "UNKNOWN" : r.getSource().name();
            channel.merge(key, 1, Integer::sum);
        }

        // No-show / imtina dərəcəsi
        long noShow = reservations.stream().filter(r -> r.getStatus() != null && NO_SHOW.contains(r.getStatus())).count();
        double noShowRate = total == 0 ? 0.0 : round2((double) noShow / total);

        // Doluluq: qəbul olunmuş rezervlər / (masa sayı * fərqli günlər)
        long accepted = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count();
        long distinctDays = reservations.stream().map(ReservationDto::getDate).filter(Objects::nonNull).distinct().count();
        double occupancy = 0.0;
        if (totalTables > 0 && distinctDays > 0) {
            occupancy = round2(100.0 * accepted / ((double) totalTables * distinctDays));
            if (occupancy > 100) occupancy = 100;
        }

        // Aylıq trend (son 6 ay)
        Map<YearMonth, Integer> byMonth = new TreeMap<>();
        int monthlyReservationsCount = 0;
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        int previousMonthReservationsCount = 0;
        for (ReservationDto r : reservations) {
            if (r.getDate() != null) {
                YearMonth ym = YearMonth.from(r.getDate());
                byMonth.merge(ym, 1, Integer::sum);
                if (ym.equals(currentMonth)) {
                    monthlyReservationsCount++;
                } else if (ym.equals(previousMonth)) {
                    previousMonthReservationsCount++;
                }
            }
        }
        List<AiSummaryDto.MonthlyPoint> trend = byMonth.entrySet().stream()
                .skip(Math.max(0, byMonth.size() - 6))
                .map(e -> new AiSummaryDto.MonthlyPoint(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());

        // Bu ay / keçən ay artım faizi (keçən ay data yoxdursa müqayisə mümkün deyil -> null)
        Double growthPercent = previousMonthReservationsCount == 0 ? null
                : round2(100.0 * (monthlyReservationsCount - previousMonthReservationsCount) / previousMonthReservationsCount);

        return AiSummaryDto.builder()
                .totalReservations(total)
                .totalCapacity(totalCapacity)
                .avgOccupancyPercent(occupancy)
                .peakHours(peakHours)
                .channelSplit(channel)
                .noShowRate(noShowRate)
                .monthlyTrend(trend)
                .branchCount(branches != null ? branches.size() : 0)
                .tableCount(totalTables)
                .monthlyReservations(monthlyReservationsCount)
                .previousMonthReservations(previousMonthReservationsCount)
                .reservationsGrowthPercent(growthPercent)
                .build();
    }

    private <T> T data(ApiResponse<T> resp) {
        return resp == null ? null : resp.getData();
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
